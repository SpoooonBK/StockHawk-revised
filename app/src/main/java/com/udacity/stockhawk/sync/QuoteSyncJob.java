package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.utilities.DateRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import rx.Observable;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;
    public static final String ACTION_STOCK_NOT_FOUND = "com.udacity.stockhawk.ACTION_STOCK_NOT_FOUND";
    public static final String STOCK_SYMBOL = "stock_symbol";

    private QuoteSyncJob() {
    }


    public static Observable<String> getHistoryStringObservable(final String symbol, final DateRange dateRange) {

        Observable<String> observable = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getHistory(symbol, dateRange);
            }
        });
        return observable;
    }


    public static String getHistory(String symbol, DateRange dateRange) {

        Timber.v("Requesting quotes: " + symbol + dateRange.toString());

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.setTime(dateRange.getFrom());

        //Set the "to" value to a week later
        to.setTime(dateRange.getTo());


        try {
            Stock stock = YahooFinance.get(symbol, from, to, Interval.DAILY);
            List<HistoricalQuote> history = stock.getHistory(from, to, Interval.DAILY);

            StringBuilder historyBuilder = new StringBuilder();

            for (HistoricalQuote it : history) {
                historyBuilder.append(it.getDate().getTimeInMillis());
                historyBuilder.append(", ");
                historyBuilder.append(it.getClose());
                historyBuilder.append("\n");
            }


            return historyBuilder.toString();

        } catch (IOException e) {
            Timber.e(e, "Error fetching stock quotes");
        }
        return null;
    }

    static void getQuotes(Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);


        try {

            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

            Timber.d(stockCopy.toString());

            if (stockArray.length == 0) {
                return;
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            Timber.d(quotes.toString());

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                String symbol = iterator.next();


                Stock stock = quotes.get(symbol);
                

                if (stock != null && stock.getName() != null) {
                    StockQuote quote = stock.getQuote();

                    float price = quote.getPrice().floatValue();
                    float change = quote.getChange().floatValue();
                    float percentChange = quote.getChangeInPercent().floatValue();


                    // WARNING! Don't request historical data for a stock that doesn't exist!
                    // The request will hang forever X_x
                    List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                    StringBuilder historyBuilder = new StringBuilder();

                    for (HistoricalQuote it : history) {
                        historyBuilder.append(it.getDate().getTimeInMillis());
                        historyBuilder.append(", ");
                        historyBuilder.append(it.getClose());
                        historyBuilder.append("\n");
                    }

                    ContentValues quoteCV = new ContentValues();
                    quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                    quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                    quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                    quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);


                    quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

                    quoteCVs.add(quoteCV);

                } else {
                    throw new StockNotFoundException(symbol);
                }


            }

            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        } catch (StockNotFoundException e) {
            Timber.e(e, "Stock not found: " + e.getSymbol());
            Intent stockNotFoundIntent = new Intent(ACTION_STOCK_NOT_FOUND);
            stockNotFoundIntent.putExtra(STOCK_SYMBOL, e.getSymbol());
            PrefUtils.removeStock(context, e.getSymbol());
            context.sendBroadcast(stockNotFoundIntent);
        }

    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }


}
