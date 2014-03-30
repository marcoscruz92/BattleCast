package team.bazinga.battle_cast;



import java.io.IOException;

import team.bazinga.sumo_balls_cast.R;



import android.app.Dialog;

import android.os.Bundle;

import android.support.v4.view.GestureDetectorCompat;

import android.support.v4.view.MenuItemCompat;

import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.MediaRouteActionProvider;

import android.support.v7.media.MediaRouteSelector;

import android.support.v7.media.MediaRouter;

import android.support.v7.media.MediaRouter.RouteInfo;

import android.util.Log;

import android.view.GestureDetector.OnDoubleTapListener;

import android.view.GestureDetector.OnGestureListener;

import android.view.Menu;

import android.view.MenuItem;

import android.view.MotionEvent;

import android.widget.Button;

import android.widget.ImageView;

import android.widget.Toast;

import android.view.View;

import com.google.android.gms.cast.ApplicationMetadata;

import com.google.android.gms.cast.Cast;

import com.google.android.gms.cast.Cast.ApplicationConnectionResult;

import com.google.android.gms.cast.Cast.MessageReceivedCallback;

import com.google.android.gms.cast.CastDevice;

import com.google.android.gms.cast.CastMediaControlIntent;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.api.Status;



public class MainActivity extends ActionBarActivity implements OnGestureListener, OnDoubleTapListener {



	private static final String TAG = MainActivity.class.getSimpleName();



	private MediaRouter mMediaRouter;

	private MediaRouteSelector mMediaRouteSelector;

	private MediaRouter.Callback mMediaRouterCallback;

	private CastDevice mSelectedDevice;

	private GoogleApiClient mApiClient;

	private Cast.Listener mCastListener;

	private ConnectionCallbacks mConnectionCallbacks;

	private ConnectionFailedListener mConnectionFailedListener;

	private SumoCast sumoCast;

	private boolean mApplicationStarted;

	private boolean mWaitingForReconnect;

	private static final String DEBUG_TAG = "THIS APP";

	private GestureDetectorCompat mDetector;

	private Dialog dialog;

	private Button about;

	private ImageView apple_image;

	private ImageView android_image;



	String SIDE = "Left";
	boolean isTeamselected = false;


	@Override

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//setContentView(R.layout.activity_main);

		setContentView(R.layout.welcome_cast);

		mMediaRouter = MediaRouter.getInstance(getApplicationContext());

		mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(getResources().getString(R.string.app_id))).build();

		mMediaRouterCallback = new MyMediaRouterCallback();

		mDetector = new GestureDetectorCompat(this,this);

		mDetector.setOnDoubleTapListener(this);


		apple_image = (ImageView) findViewById(R.id.apple);

		android_image = (ImageView) findViewById(R.id.android);


		about = (Button) findViewById(R.id.bAbout);

		about.setOnClickListener( new View.OnClickListener() {

			@Override

			public void onClick(View v) {	        

				launchAboutDialog(v);

			}

		});

		apple_image.setOnClickListener( new View.OnClickListener() {

			@Override

			public void onClick(View v) {	        

				SIDE = "Right";

				apple_image.setImageResource(R.drawable.apple);

				android_image.setImageResource(R.drawable.android_clicked);
				isTeamselected = true;
				Toast.makeText(getApplicationContext(), "You are in team Apple", Toast.LENGTH_SHORT).show();

			}

		});

		android_image.setOnClickListener( new View.OnClickListener() {

			@Override

			public void onClick(View v) {	        

				SIDE = "Left";

				apple_image.setImageResource(R.drawable.apple_clicked);

				android_image.setImageResource(R.drawable.android);
				isTeamselected = true;
				Toast.makeText(getApplicationContext(), "You are in team Android", Toast.LENGTH_SHORT).show();

			}

		});

	}


	private void launchAboutDialog(View v) {
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.dialog_about);
		dialog.setTitle("Developed by Bazinga!");
		Button ok = (Button) dialog.findViewById(R.id.bOk);
		dialog.show();
		ok.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {	        
				dialog.dismiss();
			}
		});
	}

	@Override

	protected void onResume() {

		super.onResume();

		mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);

		//about = (Button) findViewById(R.id.bAbout);
		apple_image = (ImageView) findViewById(R.id.apple);

		android_image = (ImageView) findViewById(R.id.android);


		about = (Button) findViewById(R.id.bAbout);

		about.setOnClickListener( new View.OnClickListener() {

			@Override

			public void onClick(View v) {	        

				launchAboutDialog(v);

			}

		});
		
		apple_image.setOnClickListener( new View.OnClickListener() {

			@Override

			public void onClick(View v) {	        

				SIDE = "Right";

				apple_image.setImageResource(R.drawable.apple);

				android_image.setImageResource(R.drawable.android_clicked);
				isTeamselected = true;
				Toast.makeText(getApplicationContext(), "You are in team Apple", Toast.LENGTH_SHORT).show();

			}

		});

		android_image.setOnClickListener( new View.OnClickListener() {

			@Override

			public void onClick(View v) {	        

				SIDE = "Left";

				apple_image.setImageResource(R.drawable.apple_clicked);

				android_image.setImageResource(R.drawable.android);
				isTeamselected = true;
				Toast.makeText(getApplicationContext(), "You are in team Android", Toast.LENGTH_SHORT).show();

			}

		});

	}



	@Override

	protected void onPause() {

		if (isFinishing()) {

			mMediaRouter.removeCallback(mMediaRouterCallback);

		}

		super.onPause();

	}



	@Override

	public void onDestroy() {

		teardown();

		super.onDestroy();

	}


	@Override

	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.main, menu);

		MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);

		MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);

		mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

		return true;

	}

	private class MyMediaRouterCallback extends MediaRouter.Callback {

		@Override  //Taping view

		public void onRouteSelected(MediaRouter router, RouteInfo info) {

			if(!(SIDE.equals(""))) {

				setContentView(R.layout.activity_main);

				Log.d(TAG, "onRouteSelected");

				mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
				
				if(!isTeamselected)
					Toast.makeText(getApplicationContext(), "You are in team Android", Toast.LENGTH_SHORT).show();

				Button tap = (Button) findViewById(R.id.bTap);

				tap.setOnClickListener( new View.OnClickListener() {

					@Override

					public void onClick(View v) {	        

						Log.i(DEBUG_TAG, "TOUCHED");
						sendMessage(SIDE);

					}

				});

				launchReceiver();

			} else {

				Toast.makeText(getApplicationContext(), "Type your name & choose a side!", Toast.LENGTH_SHORT).show();

			}

		}


		@Override  //Logo view

		public void onRouteUnselected(MediaRouter router, RouteInfo info) {

			setContentView(R.layout.welcome_cast);

			Log.d(TAG, "onRouteUnselected: info=" + info);
			
			apple_image = (ImageView) findViewById(R.id.apple);

			android_image = (ImageView) findViewById(R.id.android);


			about = (Button) findViewById(R.id.bAbout);

			about.setOnClickListener( new View.OnClickListener() {

				@Override

				public void onClick(View v) {	        

					launchAboutDialog(v);

				}

			});

			apple_image.setOnClickListener( new View.OnClickListener() {

				@Override

				public void onClick(View v) {	        

					SIDE = "Right";

					apple_image.setImageResource(R.drawable.apple);

					android_image.setImageResource(R.drawable.android_clicked);
					isTeamselected = true;
					Toast.makeText(getApplicationContext(), "You are in team Apple", Toast.LENGTH_SHORT).show();

				}

			});

			android_image.setOnClickListener( new View.OnClickListener() {

				@Override

				public void onClick(View v) {	        

					SIDE = "Left";

					apple_image.setImageResource(R.drawable.apple_clicked);

					android_image.setImageResource(R.drawable.android);
					isTeamselected = true;
					Toast.makeText(getApplicationContext(), "You are in team Android", Toast.LENGTH_SHORT).show();

				}

			});

			teardown();

			mSelectedDevice = null;

		}

	}



	private void launchReceiver() {

		try {

			mCastListener = new Cast.Listener() {

				@Override

				public void onApplicationDisconnected(int errorCode) {

					Log.d(TAG, "application has stopped");

					teardown();

				}

			};

			mConnectionCallbacks = new ConnectionCallbacks();

			mConnectionFailedListener = new ConnectionFailedListener();


			Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(mSelectedDevice, mCastListener);


			mApiClient = new GoogleApiClient.Builder(this)

			.addApi(Cast.API, apiOptionsBuilder.build())

			.addConnectionCallbacks(mConnectionCallbacks)

			.addOnConnectionFailedListener(mConnectionFailedListener)

			.build();



			mApiClient.connect();

		} catch (Exception e) {

			Log.e(TAG, "Failed launchReceiver", e);

		}

	}



	private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

		@Override

		public void onConnected(Bundle connectionHint) {

			Log.d(TAG, "onConnected");



			if (mApiClient == null) {

				return;

			}



			try {

				if (mWaitingForReconnect) {

					mWaitingForReconnect = false;


					if ((connectionHint != null)

							&& connectionHint

							.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {

						Log.d(TAG, "App  is no longer running");

						teardown();

					} else {



						try {

							Cast.CastApi.setMessageReceivedCallbacks(

									mApiClient,

									sumoCast.getNamespace(),

									sumoCast);

						} catch (IOException e) {

							Log.e(TAG, "Exception while creating channel", e);

						}

					}

				} else {

					Cast.CastApi.launchApplication(mApiClient, getString(R.string.app_id), false)

					.setResultCallback(new ResultCallback<Cast.ApplicationConnectionResult>() {

						@Override

						public void onResult(ApplicationConnectionResult result) {

							Status status = result.getStatus();

							Log.d(TAG,

									"ApplicationConnectionResultCallback.onResult: statusCode"

+ status.getStatusCode());

							if (status.isSuccess()) {

								ApplicationMetadata applicationMetadata = result.getApplicationMetadata();

								String sessionId = result.getSessionId();

								String applicationStatus = result.getApplicationStatus();

								boolean wasLaunched = result.getWasLaunched();


								mApplicationStarted = true;

								sumoCast = new SumoCast();

								try {

									Cast.CastApi

									.setMessageReceivedCallbacks(

											mApiClient,

											sumoCast.getNamespace(), sumoCast);

								} catch (IOException e) {

									Log.e(TAG, "Exception while creating channel", e);

								}

							} else {

								Log.e(TAG, "application could not launch");

								teardown();

							}

						}

					});

				}

			} catch (Exception e) {

				Log.e(TAG, "Failed to launch application", e);

			}

		}



		@Override

		public void onConnectionSuspended(int cause) {

			Log.d(TAG, "onConnectionSuspended");

			mWaitingForReconnect = true;

		}

	}

	private class ConnectionFailedListener implements

	GoogleApiClient.OnConnectionFailedListener {

		@Override

		public void onConnectionFailed(ConnectionResult result) {

			Log.e(TAG, "onConnectionFailed ");

			teardown();

		}

	}



	private void teardown() {

		Log.d(TAG, "teardown");

		if (mApiClient != null) {

			if (mApplicationStarted) {

				if (mApiClient.isConnected()) {

					try {

						Cast.CastApi.stopApplication(mApiClient);

						if (sumoCast != null) {

							Cast.CastApi.removeMessageReceivedCallbacks(

									mApiClient,

									sumoCast.getNamespace());

							sumoCast = null;

						}

					} catch (IOException e) {

						Log.e(TAG, "Exception while removing channel", e);

					}

					mApiClient.disconnect();

				}

				mApplicationStarted = false;

			}

			mApiClient = null;

		}

		mSelectedDevice = null;

		mWaitingForReconnect = false;

	}



	private void sendMessage(String message) {

		if (mApiClient != null && sumoCast != null) {

			try {

				Cast.CastApi.sendMessage(mApiClient, sumoCast.getNamespace(), message)

				.setResultCallback(new ResultCallback<Status>() {

					@Override

					public void onResult(Status result) {

						if (!result.isSuccess()) {

							Log.e(TAG, "Sending message failed");

						}

					}

				});

			} catch (Exception e) {

				Log.e(TAG, "Exception while sending message", e);

			}

		} else {

			Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)

			.show();

		}

	}



	class SumoCast implements MessageReceivedCallback {



		public String getNamespace() {

			return getString(R.string.namespace);

		}

		@Override

		public void onMessageReceived(CastDevice castDevice, String namespace,

				String message) {

			Log.d(TAG, "onMessageReceived: " + message);
			if(message == "You Win"){
				Toast.makeText(getApplicationContext(), "YaY you WIN", 
						   Toast.LENGTH_LONG).show();
			}
			
		}

	}


	@Override 

	public boolean onTouchEvent(MotionEvent event){ 

		this.mDetector.onTouchEvent(event);

		return super.onTouchEvent(event);

	}



	@Override

	public boolean onSingleTapConfirmed(MotionEvent e) {

		// TODO Auto-generated method stub

		return false;

	}



	@Override

	public boolean onDoubleTap(MotionEvent e) {

		// TODO Auto-generated method stub

		return false;

	}



	@Override

	public boolean onDoubleTapEvent(MotionEvent e) {

		// TODO Auto-generated method stub

		return false;

	}



	int times = 0;

	@Override

	public boolean onDown(MotionEvent e) {

		/*Log.i(DEBUG_TAG, "TOUCHED");

Toast.makeText(getApplicationContext(), "TOUCHED", Toast.LENGTH_SHORT).show();

times++;

sendMessage("TOUCHED: " + times);*/

		return false;

	}



	@Override

	public void onShowPress(MotionEvent e) {

		// TODO Auto-generated method stub


	}



	@Override

	public boolean onSingleTapUp(MotionEvent e) {

		// TODO Auto-generated method stub

		return false;

	}



	@Override

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,

			float distanceY) {

		// TODO Auto-generated method stub

		return false;

	}



	@Override

	public void onLongPress(MotionEvent e) {

		// TODO Auto-generated method stub


	}



	@Override

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		// TODO Auto-generated method stub

		return true;



	}

}