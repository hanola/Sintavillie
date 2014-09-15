package no.olav.samples.facedetect;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class IntroActivity extends Activity {

	
	ImageButton nextbutton;
	ImageButton startbutton;
	ImageView infoImage;
	int i = 0; 
	
	int infoImages[] = {R.drawable.sintavillie, R.drawable.inverted, R.drawable.end, R.drawable.w6};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		addButtonListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.intro, menu);
		return true;
	}
	
	protected void chooseLevel(View view){
		Intent intentLevels = new Intent(this, no.olav.samples.facedetect.MainActivity.class);
		startActivity(intentLevels);
	}
	
	public void addButtonListener(){
		
		infoImage = (ImageView)findViewById(R.id.imageView1);
		
		nextbutton = (ImageButton)findViewById(R.id.next);
		nextbutton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v){
				SnapShot sn = new SnapShot(v);
				Bitmap b = sn.snap();
				Log.d("Snap", "result: "+b);
				infoImage.setImageResource(infoImages[i++]);
				if (i==4){
					i=0;
					nextbutton.setVisibility(View.INVISIBLE);
					startbutton.setVisibility(View.VISIBLE);	
				}
			}
		});
		
		startbutton = (ImageButton)findViewById(R.id.startGame);
		startbutton.setVisibility(View.INVISIBLE);	
		startbutton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v){
				chooseLevel(v);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
