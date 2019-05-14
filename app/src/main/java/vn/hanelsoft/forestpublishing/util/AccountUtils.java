package vn.hanelsoft.forestpublishing.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import vn.hanelsoft.forestpublishing.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dinhdv on 7/31/2017.
 */

public class AccountUtils {

    public static Account getAccountUser(Activity activity, AccountManager manager) {
        Account[] accounts = manager.getAccounts();
        Account accountResult = null;
        for (Account account : accounts) {
            String name = account.name;
            String type = account.type;
            if (type.equalsIgnoreCase(activity.getString(R.string.account_type))) {
                Log.e("Account name", "Values :" + name);
                accountResult = account;
            }
        }
        return accountResult;
    }

    /**
     * Change email of user.
     * get old password.
     * remove old account.
     * save with new email , old password.
     *
     * @param activity
     * @param mManagerAccount
     * @param email
     */
    public static void changeEmailAddress(Activity activity, AccountManager mManagerAccount, String email) {
        Account accountDefault = getAccountUser(activity, mManagerAccount);
        if (accountDefault == null)
            return;
        String password = mManagerAccount.getPassword(accountDefault);
        saveAccountInformation(activity, mManagerAccount, email, password);
    }

    public static void changePasswordDefault(Activity activity, AccountManager mManagerAccount, String password) {
        Account accountDefault = getAccountUser(activity, mManagerAccount);
        if (accountDefault == null)
            return;
        mManagerAccount.setPassword(accountDefault, password);
    }

    public static String getPassword(Activity activity, AccountManager mManagerAccount) {
        Account accountDefault = getAccountUser(activity, mManagerAccount);
        if (accountDefault == null)
            return "";
        return mManagerAccount.getPassword(accountDefault);
    }

    /**
     * Change password of user
     *
     * @param activity
     * @param mManagerAccount
     * @param newPassword
     */
    public static void changePasswordUser(Activity activity, AccountManager mManagerAccount, String userName, String newPassword) {
//        Account accountDefault = getAccountUser(activity, mManagerAccount);
//        if (accountDefault == null)
//            return;
//        String name = mManagerAccount.getPassword(accountDefault);
        saveAccountInformation(activity, mManagerAccount, userName, newPassword);
    }


    public static void saveAccountInformation(Activity activity, AccountManager mManagerAccount, String email, String password) {
        clearAllAccountOfThisApplication(activity, mManagerAccount);
        String accountType = activity.getString(R.string.account_type);
        // This is the magic that addes the account to the Android Account Manager
        final Account account = new Account(email, accountType);
        mManagerAccount.addAccountExplicitly(account, password, null);
        // Now we tell our caller, could be the Android Account Manager or even our own application
        // that the process was successful
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
//        activity.setAccountAuthenticatorResult(intent.getExtras());
        activity.setResult(RESULT_OK, intent);
//        this.finish();
//        activity.finish();
    }

    public static void clearAllAccountOfThisApplication(Activity activity, AccountManager mManagerAccount) {
        String accountType = activity.getString(R.string.account_type);
        Account[] accounts = mManagerAccount.getAccounts();
        if (accounts.length > 0) {
            for (int index = 0; index < accounts.length; index++) {
                if (accounts[index].type.intern().equalsIgnoreCase(accountType))
                    mManagerAccount.removeAccount(accounts[index], new AccountManagerCallback<Boolean>() {
                        @Override
                        public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                            Log.e("Values", "------" + accountManagerFuture);
                        }
                    }, null);
            }
        }
    }
}
