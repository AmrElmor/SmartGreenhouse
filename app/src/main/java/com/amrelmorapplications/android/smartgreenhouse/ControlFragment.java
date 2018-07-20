package com.amrelmorapplications.android.smartgreenhouse;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ControlFragment extends Fragment {

    private static final String LOG_TAG = ControlFragment.class.getName();
    private TextView fanStatusTextView;
    private TextView lampStatusTextView;
    private TextView pumpStatusTextView;
    private RequestQueue mQueue;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.control_activity, container, false);

        mQueue = Volley.newRequestQueue(getActivity());

        createNotificationChannel();

        jsonParseFanStatus();
        jsonParseLampStatus();
        jsonParsePumpStatus();

        return rootView;

    }

    private void postRequestManualMode() {
        String url = "http://192.168.137.1:3000/manual/";
        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        // Create an explicit intent for an Activity in your app

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("manual", "ON");
                return params;
            }
        };
        mQueue.add(postRequest);
    }


    private void jsonParseFanStatus() {

        String url = "http://192.168.137.1:3000/fano/last";
        createNotificationChannel();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String fanStatus = response.getString("status");

                            fanStatusTextView = getActivity().findViewById(R.id.fan_status);
                            fanStatusTextView.setText(fanStatus);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        fanStatusTextView = getActivity().findViewById(R.id.fan_status);
                        fanStatusTextView.setText(R.string.error);
                    }
                });
        mQueue.add(jsonObjectRequest);
    }

    private void jsonParseLampStatus() {

        String url = "http://192.168.137.1:3000/ledo/last";
        createNotificationChannel();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            String lampStatus = response.getString("status");

                            lampStatusTextView = getActivity().findViewById(R.id.lamp_status);
                            lampStatusTextView.setText(lampStatus);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        lampStatusTextView = getActivity().findViewById(R.id.lamp_status);
                        lampStatusTextView.setText(R.string.error);
                    }
                });
        mQueue.add(jsonObjectRequest);
    }

    private void jsonParsePumpStatus() {

        String url = "http://192.168.137.1:3000/pumpo/last";
        createNotificationChannel();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String pumpStatus = response.getString("status");

                            pumpStatusTextView = getActivity().findViewById(R.id.pump_status);
                            pumpStatusTextView.setText(pumpStatus);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        pumpStatusTextView = getActivity().findViewById(R.id.pump_status);
                        pumpStatusTextView.setText(R.string.error);
                    }
                });
        mQueue.add(jsonObjectRequest);
    }

    private void postRequestFanStart() {
        String url = "http://192.168.137.1:3000/fan/";
        createNotificationChannel();
        final String CHANNEL_ID = "1";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("FAN IS STARTED ...")
                                .setContentText("DON'T FORGET TO CLOSE IT.")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                        notificationManager.notify(1, mBuilder.build());
                        notificationManager.cancel(2);
                        notificationManager.cancel(7);
                        notificationManager.cancel(8);
                        notificationManager.cancel(9);
                        notificationManager.cancel(10);
                        notificationManager.cancel(11);
                        notificationManager.cancel(12);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                                // Create an explicit intent for an Activity in your app
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("NOTHING HAPPENED ...")
                                        .setContentText("CHECK NETWORK.")
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                                notificationManager.notify(7, mBuilder.build());
                                notificationManager.cancel(8);
                                notificationManager.cancel(9);
                                notificationManager.cancel(10);
                                notificationManager.cancel(11);
                                notificationManager.cancel(12);
                            }
                        }
                ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", "ON");

                return params;
            }
        };
        mQueue.add(postRequest);
    }

    private void postRequestFanStop() {
        String url = "http://192.168.137.1:3000/fan/";
        createNotificationChannel();
        final String CHANNEL_ID = "2";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("FAN IS STOPPED ...")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                        notificationManager.notify(2, mBuilder.build());
                        notificationManager.cancel(1);
                        notificationManager.cancel(7);
                        notificationManager.cancel(8);
                        notificationManager.cancel(9);
                        notificationManager.cancel(10);
                        notificationManager.cancel(11);
                        notificationManager.cancel(12);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("NOTHING HAPPENED ...")
                                        .setContentText("CHECK NETWORK.")
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                                notificationManager.notify(8, mBuilder.build());
                                notificationManager.cancel(7);
                                notificationManager.cancel(9);
                                notificationManager.cancel(10);
                                notificationManager.cancel(11);
                                notificationManager.cancel(12);
                            }
                        }
                ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", "OFF");

                return params;
            }
        };
        mQueue.add(postRequest);
    }

    private void postRequestLampStart() {
        String url = "http://192.168.137.1:3000/led/";
        createNotificationChannel();
        final String CHANNEL_ID = "3";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("LAMP IS STARTED ...")
                                .setContentText("DON'T FORGET TO CLOSE IT.")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                        notificationManager.notify(3, mBuilder.build());
                        notificationManager.cancel(4);
                        notificationManager.cancel(7);
                        notificationManager.cancel(8);
                        notificationManager.cancel(9);
                        notificationManager.cancel(10);
                        notificationManager.cancel(11);
                        notificationManager.cancel(12);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("NOTHING HAPPENED ...")
                                        .setContentText("CHECK NETWORK.")
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                                notificationManager.notify(9, mBuilder.build());
                                notificationManager.cancel(7);
                                notificationManager.cancel(8);
                                notificationManager.cancel(10);
                                notificationManager.cancel(11);
                                notificationManager.cancel(12);
                            }
                        }
                ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", "ON");

                return params;
            }
        };
        mQueue.add(postRequest);
    }

    private void postRequestLampStop() {
        String url = "http://192.168.137.1:3000/led/";
        createNotificationChannel();
        final String CHANNEL_ID = "4";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("LAMP IS STOPPED ...")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                        notificationManager.notify(4, mBuilder.build());
                        notificationManager.cancel(3);
                        notificationManager.cancel(7);
                        notificationManager.cancel(8);
                        notificationManager.cancel(9);
                        notificationManager.cancel(10);
                        notificationManager.cancel(11);
                        notificationManager.cancel(12);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("NOTHING HAPPENED ...")
                                        .setContentText("CHECK NETWORK.")
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                                notificationManager.notify(10, mBuilder.build());
                                notificationManager.cancel(7);
                                notificationManager.cancel(8);
                                notificationManager.cancel(9);
                                notificationManager.cancel(11);
                                notificationManager.cancel(12);
                            }
                        }
                ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", "OFF");

                return params;
            }
        };
        mQueue.add(postRequest);
    }

    private void postRequestPumpStart() {
        String url = "http://192.168.137.1:3000/pump/";
        createNotificationChannel();
        final String CHANNEL_ID = "5";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("PUMP IS STARTED ...")
                                .setContentText("DON'T FORGET TO CLOSE IT.")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                        notificationManager.notify(5, mBuilder.build());
                        notificationManager.cancel(6);
                        notificationManager.cancel(7);
                        notificationManager.cancel(8);
                        notificationManager.cancel(9);
                        notificationManager.cancel(10);
                        notificationManager.cancel(11);
                        notificationManager.cancel(12);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("NOTHING HAPPENED ...")
                                        .setContentText("CHECK NETWORK.")
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                                notificationManager.notify(11, mBuilder.build());
                                notificationManager.cancel(7);
                                notificationManager.cancel(8);
                                notificationManager.cancel(9);
                                notificationManager.cancel(10);
                                notificationManager.cancel(12);
                            }
                        }
                ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", "ON");

                return params;
            }
        };
        mQueue.add(postRequest);
    }

    private void postRequestPumpStop() {
        String url = "http://192.168.137.1:3000/pump/";
        createNotificationChannel();
        final String CHANNEL_ID = "6";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("PUMP IS STOPPED ...")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                        notificationManager.notify(6, mBuilder.build());
                        notificationManager.cancel(5);
                        notificationManager.cancel(7);
                        notificationManager.cancel(8);
                        notificationManager.cancel(9);
                        notificationManager.cancel(10);
                        notificationManager.cancel(11);
                        notificationManager.cancel(12);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("NOTHING HAPPENED ...")
                                        .setContentText("CHECK NETWORK.")
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
                                notificationManager.notify(12, mBuilder.build());
                                notificationManager.cancel(7);
                                notificationManager.cancel(8);
                                notificationManager.cancel(9);
                                notificationManager.cancel(10);
                                notificationManager.cancel(11);
                            }
                        }
                ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", "OFF");
                return params;
            }
        };
        mQueue.add(postRequest);
    }

    private void createNotificationChannel() {
        final String CHANNEL_ID = "0";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void myUpdateOperation() {
        createNotificationChannel();
        jsonParsePumpStatus();
        jsonParseLampStatus();
        jsonParseFanStatus();
        mySwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        createNotificationChannel();
        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        myUpdateOperation();
                    }
                }
        );

        Button fanStartButton = getActivity().findViewById(R.id.start_fan);
        Button fanStopButton = getActivity().findViewById(R.id.stop_fan);
        Button lampStartButton = getActivity().findViewById(R.id.start_lamp);
        Button lampStopButton = getActivity().findViewById(R.id.stop_lamp);
        Button pumpStartButton = getActivity().findViewById(R.id.start_pump);
        Button pumpStopButton = getActivity().findViewById(R.id.stop_pump);


        fanStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRequestManualMode();
                postRequestFanStart();
                jsonParseFanStatus();
            }
        });

        fanStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRequestManualMode();
                postRequestFanStop();
                jsonParseFanStatus();
            }
        });

        lampStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRequestManualMode();
                postRequestLampStart();
                jsonParseLampStatus();
            }
        });
        lampStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRequestManualMode();
                postRequestLampStop();
                jsonParseLampStatus();
            }
        });
        pumpStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRequestManualMode();
                postRequestPumpStart();
                jsonParsePumpStatus();
            }
        });
        pumpStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRequestManualMode();
                postRequestPumpStop();
                jsonParsePumpStatus();
            }
        });
    }
}

