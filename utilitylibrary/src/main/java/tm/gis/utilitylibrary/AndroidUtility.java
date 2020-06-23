package tm.gis.utilitylibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AndroidUtility {

    /**
     *  跳出訊號框
     *
     * @param context
     * @param message
     * @param message
     * @param btnText
     */
    public static void ShowDialog(Context context,String title, String message,String btnText) {
        AlertDialog alert = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);

        alert = builder.create();

        alert.show();
    }

}
