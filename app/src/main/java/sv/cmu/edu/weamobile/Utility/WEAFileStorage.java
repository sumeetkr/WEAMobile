package sv.cmu.edu.weamobile.utility;

import android.os.Environment;

/**
 * Created by sumeet on 11/6/14.
 */
public class WEAFileStorage {
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

//    public static void saveApplicationConfiguration(Context context, String jsonAppConfiguration){
//
//        FileOutputStream outputStream;
//
//        try {
//            File file = new File(context.getFilesDir(), Constants.WEA_CONFIG_FILE_NAME_ON_DISK);
//            if(!file.exists()){
//                outputStream = context.openFileOutput(Constants.WEA_CONFIG_FILE_NAME_ON_DISK, Context.MODE_PRIVATE);
//                outputStream.write(jsonAppConfiguration.getBytes());
//                outputStream.close();
//            }else if(file.exists() && !readApplicationConfiguration(context).equals(jsonAppConfiguration)){
//                    outputStream = context.openFileOutput(Constants.WEA_CONFIG_FILE_NAME_ON_DISK, Context.MODE_PRIVATE);
//                    outputStream.write(jsonAppConfiguration.getBytes());
//                    outputStream.close();
//             }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.log(e.getMessage());
//        }
//    }

//    public static String readApplicationConfiguration(Context context ){
//        BufferedReader inputReader = null;
//        String output = "";
//        try {
//
//            inputReader = new BufferedReader(new InputStreamReader(
//                    context.openFileInput(Constants.WEA_CONFIG_FILE_NAME_ON_DISK)));
//
//            String inputString;
//            StringBuffer stringBuffer = new StringBuffer();
//            while ((inputString = inputReader.readLine()) != null) {
//                stringBuffer.append(inputString );
//            }
//
//            output = stringBuffer.toString();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//        return output;
//    }
}
