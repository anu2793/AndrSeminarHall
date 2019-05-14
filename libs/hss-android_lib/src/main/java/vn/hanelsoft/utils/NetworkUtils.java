package vn.hanelsoft.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.mylibrary.R;

public class NetworkUtils {
    public static final int TIMEOUT_DEFAULT = 30000;


    //	public enum RequestMethod {
//		GET,
//		POST,
//		PUT,
//		DELETE
//	}

    /**
     * Format an url with parameter
     *
     * @param url
     * @param params
     * @return a new url has been formatted
     */
    public static String formatUrl(String url, Map<String, Object> params) {
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder(url);
            if (!url.endsWith("?")) sb.append('?');
            for (String key : params.keySet()) {
                sb.append(key).append('=').append(params.get(key).toString()).append('&');
            }
            sb.deleteCharAt(sb.length() - 1);
            url = sb.toString();
        }
        HSSLog.i(url);
        return url;
    }

    /**
     * Check network available
     *
     * @param context
     * @return true if has connect, false if otherwise
     */
    public static boolean hasNetWork(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //
//	public static String sendRequest(String url) throws IOException{
//		return sendRequest(url, null);
//	}
//
//	public static String sendRequest(String url, List<NameValuePair> params) throws IOException {
//		return sendRequest(url, params, RequestMethod.GET, TIMEOUT_DEFAULT);
//	}
//
//	public static String sendRequest(String url,
//			List<NameValuePair> params, RequestMethod method, int timeOut) throws IOException {
//		if(TextUtils.isEmpty(url)) throw new IllegalArgumentException("URL not null");
//		if (timeOut <= 0)
//			timeOut = TIMEOUT_DEFAULT;
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpParams paramsHTTP = httpClient.getParams();
//		HttpConnectionParams.setConnectionTimeout(paramsHTTP, timeOut);
//		HttpConnectionParams.setSoTimeout(paramsHTTP, timeOut);
//		String formatParams = "";
//		if (params != null) {
//			formatParams = URLEncodedUtils.format(params, HTTP.UTF_8);
//		}
//
//		HttpUriRequest reqMethod = null;
//		switch (method) {
//		case GET:
//			reqMethod = new HttpGet(url+formatParams);
//			break;
//		case POST:
//			reqMethod = new HttpPost(url);
//			((HttpPost) reqMethod).setEntity(new UrlEncodedFormEntity(params));
//			break;
//		case PUT:
//			reqMethod = new HttpPut(url);
//			((HttpPut) reqMethod).setEntity(new UrlEncodedFormEntity(params));
//			break;
//		case DELETE:
//			reqMethod = new HttpDelete(url+formatParams);
//			break;
//		}
//		HSSLog.i(reqMethod.getURI());
//		HttpResponse resp = httpClient.execute(reqMethod);
//		if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//			reader.close();
//			return sb.toString();
//		} else {
//			HSSLog.e(resp.getStatusLine());
//		}
//		return null;
//	}
//
    public static String downloadFile(String url, String dirPath, String fileName) throws IOException {
        File target = new File(dirPath, fileName);
        if (!target.exists()) {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(TIMEOUT_DEFAULT);
            connection.setReadTimeout(TIMEOUT_DEFAULT);

//			if (connection.getResponseCode() == HttpStatus.SC_OK) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = connection.getInputStream();
                os = new FileOutputStream(target);
                byte[] buffer = new byte[8 * 1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteCount);
                }
            } finally {
                if (is != null) is.close();
                if (os != null) {
                    os.flush();
                    os.close();
                }
                connection.disconnect();
            }
//			} else {
//				HSSLog.e(connection.getResponseMessage());
//			}
        }
        return target.getAbsolutePath();
    }
//
//	public static String uploadFile(String url, File fileUpload) throws IOException{
//		String folderNameInServer = "photo";
//		String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setDoInput(true);
//        connection.setDoOutput(true);
//        connection.setUseCaches(false);
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Connection", "Keep-Alive");
//        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//        DataOutputStream dos = null;
//        FileInputStream fileInputStream = null;
//        try {
//        	dos = new DataOutputStream(connection.getOutputStream());
//        	dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\""+folderNameInServer+"\";filename=\"" +
//             		fileUpload.getName() +"\"" + lineEnd);
//            dos.writeBytes(lineEnd);
//
//            fileInputStream = new FileInputStream(fileUpload);
//            int bytesAvailable = fileInputStream.available();
//            int maxBufferSize = 8*1024;
//            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            byte[] buffer = new byte[bufferSize];
//            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            while(bytesRead > 0){
//            	dos.write(buffer, 0, bufferSize);
//            	bytesAvailable = fileInputStream.available();
//            	bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0,bufferSize);
//            };
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//            fileInputStream.close();
//            dos.flush();
//            HSSLog.i(connection.getResponseCode()+" "+connection.getResponseMessage());
//            /**
//             * Read response from server
//             */
//            InputStream is = connection.getInputStream();
//            StringBuilder sb = new StringBuilder();
//            int ch;
//            while((ch = is.read()) != -1 ){
//            	sb.append( (char)ch );
//            }
//            is.close();
//            String resp = sb.toString();
//            HSSLog.i(resp);
//            return resp;
//		} finally {
//			if(dos != null)dos.close();
//			connection.disconnect();
//		}
//	}

    public static void showDialogError(final Activity activity, final Exception e) {
        showDialogError(activity, e, null, null, null, null);
    }

    public static void showDialogError(final Activity activity, final Exception e, View.OnClickListener action) {
        showDialogError(activity, e, null, action, null, null);
    }

    public static void showDialogError(final Activity activity, final Exception e, final String btnOk, final View.OnClickListener actionOk, final String btnCancel, final View.OnClickListener actionCancel) {
        if (activity.isFinishing())
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Resources res = activity.getResources();
                String msg;
                if (!NetworkUtils.hasNetWork(activity)) {
                    msg = res.getString(R.string.error_internet_lost);
                } else if (e instanceof UnknownHostException) {
                    msg = res.getString(R.string.error_unknown_host);
                } else if (e instanceof SocketTimeoutException) {
                    msg = res.getString(R.string.error_internet_timeout);
                } else {
                    msg = res.getString(R.string.error_network_general);
                }
                HSSDialog.show(activity, msg, btnOk, actionOk, btnCancel, actionCancel);
            }
        });
    }
}
