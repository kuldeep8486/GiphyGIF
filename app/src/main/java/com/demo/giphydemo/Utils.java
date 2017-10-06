package com.demo.giphydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class Utils
{
	private String TAG = Utils.class.getSimpleName();

	/**
	 * check if string contains characters only. Use for name validation
	 * @param name
	 * @return false if contains non character number or special character, true if valid
	 */
	public static boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

	/**
	 * check if entered email address is valid
	 * @param email email address
	 * @return true if valid else false
	 */
	public static boolean validateEmail(CharSequence email)
	{
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	/**
	 * use to replace null value with blank value
	 */
	public static String getValidAPIStringResponse(String value)
	{
		if(value == null || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("<null>"))
		{
			value = "";
		}
		return value.trim();
	}

	public static String removeDiacriticalMarks(String string)
	{
		return string;
		/*try {
			return getValidAPIStringResponse(Normalizer.normalize(string, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
		} catch (Exception e) {
			e.printStackTrace();
			return getValidAPIStringResponse(string);
		}*/
	}

	/**
	 * use to get valid boolean from string
	 */
	public static boolean getValidAPIBooleanResponse(String value)
	{
		boolean flag = false;
		try {
			value = getValidAPIStringResponse(value);

			if(value.equalsIgnoreCase("true"))
			{
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * use to get valid integer from string
	 * @param value
	 * @return
	 */
	public static int getValidAPIIntegerResponse(String value)
	{
		int val = 0;
		value = getValidAPIStringResponse(value);

		if(value.contains("."))
		{
			try {
				float f = Float.parseFloat(value);
				val = (int) f;
			} catch (NumberFormatException e) {
				val = 0;
			}
		}
		else
		{
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				val = 0;
			}
		}

		return val;
	}

	/**
	 * use to get valid double from string
	 * @param value
	 * @return
	 */
	public static double getValidAPIDoubleResponse(String value)
	{
		double val = 0.0;
		value = getValidAPIStringResponse(value);

		try {
			val = Double.parseDouble(value);
		} catch (NumberFormatException e1) {
			val = 0.0;
			e1.printStackTrace();
		}

		return val;
	}

	/**
	 * use to get valid long from string
	 * @param value
	 * @return
	 */
	public static long getValidAPILongResponse(String value)
	{
		long l = 0;
		try {
			value = getValidAPIStringResponse(value);
			if(value.length() == 0)
			{
				l = 0;
			}
			else
			{
				l = Long.parseLong(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * get possibility in boolean from 0 or 1
	 * @param number 0 or 1
	 * @return true if number is 1 else false
	 */
	public static boolean getFlagFromInt(int number)
	{
		boolean flag = (number == 1) ? true : false;
		return flag;
	}

	/**
	 * get current date in dd MMM, yyyy format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDateString()
	{
		String str = "";
		try {
			Date date = new Date(Calendar.getInstance().getTimeInMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
			str = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * get string in dd MMM, yyyy format from millis
	 * @param l millis
	 * @return date string
	 */
	@SuppressLint("SimpleDateFormat")
	public static String convertDateToString(long l)
	{
		String str = "";
		try {
			Date date = new Date(l);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
			str = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	@SuppressLint("SimpleDateFormat")
	public static String convertMillisToTimeString(long l)
	{
		String str = "";
		try {
			Date date = new Date(l);
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
			str = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static long convertDateStringToMillis(final String datestr)
	{
		long millis = 0L;
		try {
			String datestrfinal = datestr;
			String[] strarr = datestr.split("/");
			datestrfinal = strarr[0] + "/" + strarr[1];
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
			Date date = dateFormat.parse(datestrfinal);
			millis = date.getTime();
		} catch (ParseException e) {
			millis = 0L;
			e.printStackTrace();
		}
		return millis;
	}

	/**
	 * get local time timestamp
	 * @param time timestamp to convert
	 * @return local timestamp
	 */
	public static long getLocalTimestamp(final long time)
	{
		long timestamp = 0;
		try {
			Date localTime = new Date(time);
			String format = "dd MMM, yyyy HH:mm:ss";
			SimpleDateFormat sdf = new SimpleDateFormat(format);

			// Convert Local Time to UTC (Works Fine)
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			@SuppressWarnings("deprecation")
            Date gmtTime = new Date(sdf.format(localTime));
			// Convert UTC to Local Time
//			Date fromGmt = new Date(gmtTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
//			System.out.println("LOCAL TIME : " + fromGmt.toString());
			timestamp = gmtTime.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	/**
	 * get relative date time like Yesterday, 4:10 PM or 2 hours ago
	 * @param activity
	 * @param timestamp timestamp to convert
	 * @return relative datetime
	 */
	public static String getRelativeDateTime(final Activity activity, final long timestamp)
	{
		String datetime = "";
		try {
			datetime = DateUtils.getRelativeDateTimeString(activity, timestamp, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();

			if(datetime.contains("/"))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, h:mm a");
				Date date = new Date(timestamp);
				datetime = sdf.format(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datetime;
	}

	/**
	 * check if url is valid or not
	 * @param url
	 * @return true if valid else false
	 */
	public static boolean validateWebUrl(CharSequence url)
	{
		try {
			return android.util.Patterns.WEB_URL.matcher(url).matches();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * check if password is valid or not
	 * @param password
	 * @return true if valid else false
	 */
	public static boolean validPassword(String password)
	{
		Pattern[] checks =
		{
			// special character
//	        Pattern.compile("[!@#\\$%^&*()~`\\-=_+\\[\\]{}|:\\\";',\\./<>?]"),
	        // small character
	        Pattern.compile("[a-z]"),
			// capital character
	        Pattern.compile("[A-Z]"),
	        // numeric character
	        Pattern.compile("\\d"),
	        // 6 to 40 character length
	        Pattern.compile("^.{6,40}$")
	    };
		boolean ok = true;
		for (Pattern check : checks)
		{
	        ok = ok && check.matcher(password).find();
	    }

		return ok;
	}

	/**
	 * hide virtual keyboard
	 * @param view edittext
	 * @param activity context
	 */
	public static void hideKeyboard(View view, Activity activity)
	{
		try
		{
			InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * show virtual keyboard
	 * @param view edittext
	 * @param activity context
	 */
	public static void showKeyboard(EditText editText, Activity activity)
	{
		try
		{
			editText.requestFocus();
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * get string with every first letter of word in capital
	 * @param s
	 * @return string
	 */
	public static String toDisplayCase(String s)
	{
		String strToReturn = "";
		try {
			s = getValidAPIStringResponse(s);
			if(s.length() == 0)
			{
				return "";
			}
			StringBuffer res = new StringBuffer();
			String[] strArr = s.split(" ");
			for (String str : strArr)
			{
			    char[] stringArray = str.trim().toCharArray();
			    stringArray[0] = Character.toUpperCase(stringArray[0]);
			    str = new String(stringArray);

			    res.append(str).append(" ");
			}
			strToReturn = res.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			final String ACTIONABLE_DELIMITERS = " -/"; // these cause the character following to be capitalized
		    StringBuilder sb = new StringBuilder();
		    boolean capNext = true;

		    for (char c : s.toCharArray())
		    {
		        c = (capNext) ? Character.toUpperCase(c) : Character.toLowerCase(c);
		        sb.append(c);
		        capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
		    }
		    strToReturn = sb.toString();
		}
		return strToReturn;
	}

	public static String toDisplayCaseMaharshi(String s)
	{
		final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following to be capitalized

		StringBuilder sb = new StringBuilder();
		boolean capNext = true;

		for (char c : s.toCharArray()) {
			c = (capNext)
					? Character.toUpperCase(c)
					: Character.toLowerCase(c);
			sb.append(c);
			capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
		}
		return sb.toString();
	}

	public static ArrayList<String> sortStringList(ArrayList<String> result)
	{
		try
		{
			Collections.sort(result, new Comparator<String>()
			{
				@Override
				public int compare(String o1, String o2)
				{
					return (o1.toLowerCase(Locale.getDefault()).compareTo(o2.toLowerCase(Locale.getDefault())));
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public static ArrayList<CategoryPojo> sortCategories(ArrayList<CategoryPojo> result)
	{
		try
		{
			Collections.sort(result, new Comparator<CategoryPojo>()
			{
				@Override
				public int compare(CategoryPojo o1, CategoryPojo o2)
				{
					return (o1.getCatName().toLowerCase(Locale.getDefault()).compareTo(o2.getCatName().toLowerCase(Locale.getDefault())));
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public static long getDateAsUpcoming(String birthdate)
	{
		long millis = 0;

		try {
			Date dateObjNow = new Date(Calendar.getInstance().getTimeInMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String[] strarrNow = dateFormat.format(dateObjNow).split("/");
			int dateNow = Integer.parseInt(strarrNow[0]);
			int monthNow = Integer.parseInt(strarrNow[1]);
			int yearNow = Integer.parseInt(strarrNow[2]);

			String[] strarr = birthdate.split("/");
			int date = Integer.parseInt(strarr[0]);
			int month = Integer.parseInt(strarr[1]);
			int year = yearNow;

			if(monthNow == month)
			{
				if(dateNow > date)
				{
					year = yearNow + 1;
				}
			}
			else if(monthNow > month)
			{
				year = yearNow + 1;
			}

			String newdatestr = String.valueOf(date) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date dateFinal = simpleDateFormat.parse(newdatestr);
			millis = dateFinal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return millis;
	}

	/**
	 * get dp value from pixels
	 * @param context activity context
	 * @param px pixels
	 * @return dp
	 */
	public static float dpFromPx(final Context context, final float px) {
	    return px / context.getResources().getDisplayMetrics().density;
	}

	/**
	 * get pixels value from dp
	 * @param context activity context
	 * @param dp dp
	 * @return pixels
	 */
	public static float pxFromDp(final Context context, final float dp) {
	    return dp * context.getResources().getDisplayMetrics().density;
	}

	/**
	 * get text in capital style
	 * @param text
	 * @return capital text
	 */
	public static String getCapitalText(String text)
	{
		String desiredText = "";
		try {
			desiredText = text.toUpperCase(Locale.ENGLISH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return desiredText;
	}

	/**
	 * get encoded string in md5 format
	 * @param input text to encode
	 * @return encoded string
	 */
	public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	public static Typeface getHeaderTypeface(final Activity activity)
	{
//		return Typeface.createFromAsset(activity.getAssets(),"BubblegumSans-Regular_0.otf");
		return Typeface.createFromAsset(activity.getAssets(),"Raleway-Medium.ttf");
	}

	public static Typeface getEdittextTypeface(final Activity activity)
	{
		return Typeface.createFromAsset(activity.getAssets(),"Raleway-Regular.ttf");
	}

	public static Typeface getEdittextNumberTypeface(final Activity activity)
	{
		return Typeface.createFromAsset(activity.getAssets(),"AvenirNextLTPro-Regular.otf");
	}

	public static Typeface getEdittextNumberMediumTypeface(final Activity activity)
	{
		return Typeface.createFromAsset(activity.getAssets(),"AvenirNextLTPro-Medium.otf");
	}

	public static Typeface getEdittextMediumTypeface(final Activity activity)
	{
		return Typeface.createFromAsset(activity.getAssets(),"Raleway-Medium.ttf");
	}

	public static Typeface getEdittextSemiBoldTypeface(final Activity activity)
	{
		return Typeface.createFromAsset(activity.getAssets(),"Raleway-SemiBold.ttf");
	}

	public static Typeface getEdittextBoldTypeface(final Activity activity)
	{
		return Typeface.createFromAsset(activity.getAssets(),"Raleway-Bold.ttf");
	}

	public static int[] getDeviceWidthHeight(final Activity activity)
	{
		int[] arr = {0, 0};
		try {
			Display display = activity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			arr[0] = width;
			arr[1] = height;
		} catch (Exception e) {
			e.printStackTrace();
			arr[0] = 0;
			arr[1] = 0;
		}
		return arr;
	}

	public static String getTotalMemoryUsedByApp()
	{
		double roundOff = 0.0;

		try {
			long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			double memoryMb = (double) memory / (1000000);
			roundOff = (double) Math.round(memoryMb * 100.0) / 100.0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(roundOff);
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
    {
        Bitmap bitmap = null;
        try
        {
			if (drawable instanceof BitmapDrawable)
			{
			    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			    if(bitmapDrawable.getBitmap() != null) {
			        return bitmapDrawable.getBitmap();
			    }
			}

			if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
			{
			    bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
			}
			else
			{
			    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			}
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return bitmap;
    }

	public static void shareViaAllApps(final String text, final Activity activity)
	{
		try {
			String appname = activity.getResources().getString(R.string.app_name);
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, appname);
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text.trim());
			activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void ShowSnackBar(Activity activity, View v, String message, String primarycolor)
	{
		Typeface tf = Typeface.createFromAsset(activity.getAssets(), "Raleway-Regular.ttf");

		Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);

		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(Color.parseColor("#FFFFFF"));
		textView.setTypeface(tf);
		sbView.setBackgroundColor(Color.parseColor(primarycolor));

		snackbar.show();
	}

	public static void writeToCustomFileHashmap(final String data, final String filename)
	{
		try
		{
			File myFileDir = new File(Environment.getExternalStorageDirectory() + "/Demo/text/");
			if(!myFileDir.exists())
			{
				myFileDir.mkdirs();
			}

			File myFile = new File(Environment.getExternalStorageDirectory() + "/Demo/text/" +  filename);
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
			outputStreamWriter.write(data);
			outputStreamWriter.close();
			// Toast.makeText(getApplicationContext(), "File saved on " + myFile.getAbsolutePath() + " ", Toast.LENGTH_LONG).show();
		}
		catch (IOException e)
		{
		}
	}
}