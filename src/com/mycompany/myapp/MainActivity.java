package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.ClipboardManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
	private String returnValues="";
	
	private Spinner spinner;
	
	private String fileName;
	private String fileDir;
	
	//private final String fileName="cv.txt";
	//private final String fileDir="/storage/emulated/0/CV/";
	private EditText editText3;
	//private TextView editText1=(EditText) findViewById(R.id.edit_message1);
	
	
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		editText3=(EditText) findViewById(R.id.edit_message3);
		//retrieve file directory+name
		loadSavedPreferences();
		
		
		assignValuesDropdown();
		
		final Button buttonOk = (Button) findViewById(R.id.buttonOk);
		buttonOk.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
		// Perform action on click
			final EditText editText1=(EditText) findViewById(R.id.edit_message1);
			
			final EditText editText2=(EditText) findViewById(R.id.edit_message2);
			
			String user=spinner.getSelectedItem().toString();
			String cardIndex=editText1.getText().toString().toUpperCase();
			//final String fileName="cv.txt";//"/storage/emulated/0/CV/cv.txt";
			//File file = new File("/storage/emulated/0/CV", fileName);
			File file = new File(fileDir, fileName);
			readCardValue(file, user,cardIndex,editText1);
			if (!returnValues.equals("")) {
				editText2.setText(returnValues);
			    editText2.clearFocus();
				editText2.requestFocus();
			    editText2.setSelection(0, editText2.getText().length());
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
				clipboard.setText(editText2.getText().toString());
			}
			
		}
		
		});	
		
		final Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					final EditText editText1=(EditText) findViewById(R.id.edit_message1);
					final EditText editText2=(EditText) findViewById(R.id.edit_message2);
					editText1.requestFocus();
					editText1.setText("");
					editText2.setText("");
					
					
		}
		
		});
		
		final Button buttonExit = (Button) findViewById(R.id.buttonExit);
		buttonExit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
				finish();	
        }
		
		});
		
		
		
		final Button buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					String fileDirAndName=editText3.getText().toString().toLowerCase();
					if (fileDirAndName.equals("")) {
						msgbox("Error","The field should be filled with directory and filename e.g.\n/storage/emulated/0/cv/cv.txt.\nTry again!");
						editText3.requestFocus();
						return;
					}
					savePreferences("fileDirAndName",fileDirAndName);
					loadSavedPreferences();

					assignValuesDropdown();
		}
		});
					
    }
	void readCardValue(File file, String user,String cardIndex,EditText editText){ 
	   //Read file in Internal Storage FileInputStream fis; 
	   String content = ""; 
		String cardIndex1;
		String cardIndex2;	 
		Integer spacePosition;
		Integer cardValuePosition;
		Integer userPosition;
		String line;
	
	   try { 
	       if (user.equals("")){
			   msgbox("Warning","No user selected! Try again.");
			   editText.requestFocus();
			   return;
		   }
		   if (cardIndex.equals("")){
			   msgbox("Warning","Fill in the card index field! Try again.");
			   editText.requestFocus();
			   return;
		   }
		  spacePosition=cardIndex.indexOf(" ");
		  if (spacePosition==-1){
			  msgbox("Warning","No space between the two card indices! Try again.");
			  editText.setText("");
			  editText.requestFocus();
			  return;
		  }
		  
		  cardIndex1=cardIndex.substring(0,spacePosition);
		  cardIndex2=cardIndex.substring(spacePosition+1,cardIndex.length());
		  String returnValue1="";
		  String returnValue2="";
		  Boolean found1=false;
		  Boolean found2=false;
		  Boolean matcherFind=false;
		  String pattern = "(.*);(.*);(.*);?";
		  // Create a Pattern object        
		  Pattern r = Pattern.compile(pattern);
		  
		  BufferedReader br = new BufferedReader(new FileReader(file));
		  while ((line = br.readLine()) != null) {			  
			  
			  Matcher m = r.matcher(line);
			  matcherFind=m.find();
			  
			  if (found1==false && matcherFind && user.equals(m.group(1)) && cardIndex1.equals(m.group(2))) {
				  returnValue1=m.group(3);
				  found1=true;
			  }
			  
			  if (found2==false && matcherFind && user.equals(m.group(1)) && cardIndex2.equals(m.group(2))) {
				  returnValue2=m.group(3);
				  found2=true;
			  }
			  
		  }
		  if (returnValue1.equals("") || returnValue2.equals("")){
			   msgbox("Warning","Card index not found in the file with card indices! Try again.");
			   editText.requestFocus();
		  }
		  returnValues=returnValue1 + returnValue2;
		  //file.close();
	      
       }
	   catch (FileNotFoundException e) {
	       e.printStackTrace();
	   }
	   catch (IOException e) {
		   e.printStackTrace();
	   }
	}
	
	void assignValuesDropdown(){
		//fill spinner with dropdown menu
		String line="";
		Integer spacePosition;
		String user="";
		String userPrevious="";
		ArrayList<String> users = new ArrayList<String>();
		File file = new File(fileDir, fileName);
		try{
		   BufferedReader br = new BufferedReader(new FileReader(file));
	       while ((line = br.readLine()) != null) {
			   spacePosition=line.indexOf(";");
			   userPrevious=user;
			   user=line.substring(0,spacePosition);
			   if (!user.equals(userPrevious)){
			      users.add(user);
			   }
			   
			   
	       }
		}
		catch (FileNotFoundException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			msgbox("Warning", sw.toString().toUpperCase());
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		spinner=(Spinner) findViewById(R.id.spinner);	
		
		
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, users);
		//ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, List_Of_Array);

		spinner.setAdapter(spinnerArrayAdapter);
		
	}
	
	void msgbox(String title,String message) { 
		new AlertDialog.Builder(this)  
		.setTitle(title) 
		.setMessage(message) 
	    .setCancelable(true)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { 
		// continue with action
		} }) 
		//.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { 
		// do nothing 
		//whichButton=0;
		//} }) 
		.show();
	    //return which;
	}
	
	void loadSavedPreferences() {
		Boolean matcherFind=false;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String fileDirAndName = sharedPreferences.getString("fileDirAndName","");
		editText3.setText(fileDirAndName);
		String pattern = "(.*)/(.*)";
		// Create a Pattern object        
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(fileDirAndName);
		matcherFind=m.find();
        if (matcherFind) {
			fileDir=m.group(1) + "/";
			fileName=m.group(2);
		} else {
			fileDir="";
			fileName="";
			editText3.setText(fileDir);
			editText3.clearFocus();
			editText3.requestFocus();
			editText3.setSelection(0, editText3.getText().length());
			msgbox("Warning","Fill in complete path and filename of file with card values.\nE.g. /storage/emulated/0/cv/cv.txt.\nThe content of the file should be in the form:\nusername;card value1 input;card value2 ouput\nE.g.:\njones842;1;5F6\njones842;2;G2A\njones842;3;7J5\nEtc.");
		}
	}
	
	void savePreferences(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
}
