package de.NeonSoft.neopowermenu.xposed;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.os.*;
import android.provider.Settings;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.*;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.*;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.services.*;
import eu.chainfire.libsuperuser.*;

import java.io.*;
import java.util.*;

public class XposedDialog extends DialogFragment {

    SharedPreferences preferences;
    SharedPreferences colorPrefs;
    SharedPreferences orderPrefs;
    SharedPreferences animationPrefs;
    
    private XposedMainActivity menuHost;

    public XposedDialog() {

    }

    static PackageManager pm;

    public Context mContext;
    public Handler mHandler;
    public LayoutInflater mInflater;
    public NotificationManager nfm;
    public Notification.Builder notifyb;

    boolean mPreviewMode = false;
    boolean mHideOnClick = false;
    boolean mKeyguardShowing = false;
    boolean mLoadAppIcons = true;
    String sStyleName;
    float mGraphicsPadding = 0;
    boolean mColorizeNonStockIcons = false;
    ImageLoader mImageLoader;
    boolean mDeepXposedLogging = false;

    boolean RequireConfirmation = false;
    String confirmDialog = "";
    boolean isDismissing = false;
    public ArrayList<String> SubDialogs = new ArrayList<>();

    boolean GraphicBehindProgress = false;

    private boolean HookShutdownThread = false;
    private boolean UseRootCommands = true;

    LinearLayout dialogMain;
    FrameLayout dialogContent;
    LinearLayout ListContainer;
    LinearLayout ListContainer2;

    FrameLayout frame;
    FrameLayout frame2;
    FrameLayout frame3;
    FrameLayout frameConfirm;
    LinearLayout frameLinear, frame2Linear, frame3Linear, frameConfirmLinear;
    private CircularRevealView revealView;
    private View selectedView;
    int backgroundColor;
    ImageView progressbg;
    ProgressBar progress;
    TextView status;
    TextView status_detail;
    TextView confirmAction;
    TextView confirmNo;
    TextView confirmYes;

    private final String SHUTDOWN_BROADCAST = "am broadcast android.intent.action.ACTION_SHUTDOWN";
    private final String SHUTDOWN = "reboot -p";
    private final String REBOOT_CMD = "reboot";
    private final String REBOOT_SOFT_REBOOT_CMD = "setprop ctl.restart zygote";
    private final String REBOOT_RECOVERY_CMD = "reboot recovery";
    private final String REBOOT_BOOTLOADER_CMD = "reboot bootloader";
    private final String[] REBOOT_SAFE_MODE = new String[]{"setprop persist.sys.safemode 1", REBOOT_SOFT_REBOOT_CMD};

    private final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;
    private final int RUNNABLE_DELAY_MS = 5000;

    public boolean canDismiss = true;

    boolean boolean_DialogGravityTop = false;
    boolean boolean_DialogGravityLeft = false;
    boolean boolean_DialogGravityRight = false;
    boolean boolean_DialogGravityBottom = false;

    AudioManager am;
    public static int amRingerMode;

    Runnable mRun;
    ArrayList<ImageView> soundModeIcon_Image = new ArrayList<>();
    ArrayList<String> soundModeIcon_Color = new ArrayList<>();
    ArrayList<TextView> soundModeIcon_Text = new ArrayList<>();
    int airplaneMode = 0;
    ArrayList<ImageView> airplaneModeIcon_Image = new ArrayList<>();
    ArrayList<String> airplaneModeIcon_Color = new ArrayList<>();
    boolean flashlightOn = false;
    ArrayList<ImageView> flashlightIcon_Image = new ArrayList<>();
    ArrayList<String> flashlightIcon_Color = new ArrayList<>();
    int rotate = 0;
    ArrayList<ImageView> rotateIcon_Image = new ArrayList<>();
    ArrayList<String> rotateIcon_Color = new ArrayList<>();
    boolean mediaPlaying = false;
    ArrayList<ImageView> playPauseIcon_Image = new ArrayList<>();
    ArrayList<String> playPauseIcon_Color = new ArrayList<>();

    ArrayList<Integer> types;
    ArrayList<String> names;
    ArrayList<String> items;
    ArrayList<Boolean> hideDesc;
    ArrayList<Boolean> hideOnLockscreen;
    ArrayList<String> texts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //doubleToConfirm = preferences.getBoolean("DoubleTouchToConfirm",true);
        mInflater = inflater;
        isDismissing = false;
        canDismiss = true;
        mHandler = new Handler();
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                airplaneMode = android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON);
            } catch (Throwable e) {
            }
        } else {
            try {
                airplaneMode = android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON);
            } catch (Throwable e) {
            }
        }
        try {
            rotate = android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Throwable e) {
        }
        View view = inflater.inflate(R.layout.fragment_power, container, false);


        mContext = getActivity();

        pm = mContext.getPackageManager();

        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        amRingerMode = am.getRingerMode();
        mediaPlaying = am.isMusicActive();

        RequireConfirmation = preferences.getBoolean("RequireConfirmation", false);
        UseRootCommands = preferences.getBoolean("UseRoot", true);

        boolean_DialogGravityTop = preferences.getBoolean("DialogGravityTop", false);
        boolean_DialogGravityLeft = preferences.getBoolean("DialogGravityLeft", false);
        boolean_DialogGravityRight = preferences.getBoolean("DialogGravityRight", false);
        boolean_DialogGravityBottom = preferences.getBoolean("DialogGravityBottom", false);

        int gravity = 0;
        if (boolean_DialogGravityTop) {
            gravity |= Gravity.TOP;
        } else if (boolean_DialogGravityBottom) {
            gravity |= Gravity.BOTTOM;
        } else {
            gravity |= Gravity.CENTER_VERTICAL;
        }
        if (boolean_DialogGravityLeft) {
            gravity |= Gravity.LEFT;
        } else if (boolean_DialogGravityRight) {
            gravity |= Gravity.RIGHT;
        } else {
            gravity |= Gravity.CENTER_HORIZONTAL;
        }
        dialogMain = (LinearLayout) view.findViewById(R.id.fragmentpowerFrameLayout_Main);

        dialogContent = (FrameLayout) view.findViewById(R.id.fragmentpowerFrameLayout1);

        revealView = (CircularRevealView) view.findViewById(R.id.reveal);
        backgroundColor = Color.parseColor(colorPrefs.getString("Dialog_Backgroundcolor", "#ffffff"));
        ListContainer = (LinearLayout) view.findViewById(R.id.ListContainer);

        frame = (FrameLayout) view.findViewById(R.id.frame);
        dialogContent.setBackgroundColor(backgroundColor);
        frameLinear = (LinearLayout) view.findViewById(R.id.frameLinear);
        frame2 = (FrameLayout) view.findViewById(R.id.frame2);
        frame2.setVisibility(View.GONE);
        frame2Linear = (LinearLayout) view.findViewById(R.id.frame2Linear);
        frame3 = (FrameLayout) view.findViewById(R.id.frame3);
        frame3.setVisibility(View.GONE);
        frame3Linear = (LinearLayout) view.findViewById(R.id.frame3Linear);
        ListContainer2 = (LinearLayout) view.findViewById(R.id.ListContainer2);

        frameConfirm = (FrameLayout) view.findViewById(R.id.frameConfirm);
        frameConfirm.setVisibility(View.GONE);
        frameConfirm.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        frameConfirmLinear = (LinearLayout) view.findViewById(R.id.frameConfirmLinear);
        confirmAction = (TextView) view.findViewById(R.id.fragmentpowerTextView_ConfirmAction);
        confirmAction.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        confirmNo = (TextView) view.findViewById(R.id.fragmentpowerTextView_ConfirmNo);
        confirmNo.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        confirmYes = (TextView) view.findViewById(R.id.fragmentpowerTextView_ConfirmYes);
        confirmYes.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));

        if (sStyleName.equalsIgnoreCase(getString(R.string.style_MaterialFullscreen))) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            dialogContent.setLayoutParams(params);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
            params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params2.topMargin = helper.getStatusBarHeight(mContext);
            if (!helper.isDeviceHorizontal(mContext)) {
                params2.bottomMargin = helper.getNavigationBarSize(mContext).y;
            } else if (helper.isDeviceHorizontal(mContext)) {
                params2.rightMargin = helper.getNavigationBarSize(mContext).x;
            }
            params2.height = FrameLayout.LayoutParams.MATCH_PARENT;
            frame.setLayoutParams(params2);
            frame3.setLayoutParams(params2);
            //params2.height = (int) helper.convertDpToPixel(150f, mContext);
            //frameConfirm.setLayoutParams(params2);
            FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
            crparams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            crparams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            revealView.setLayoutParams(crparams);
        } else if (sStyleName.equalsIgnoreCase(getString(R.string.style_MaterialFullHorizontal))) {
            FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
            crparams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            crparams.height = ((int) helper.convertDpToPixel(150, mContext)) + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));
            revealView.setLayoutParams(crparams);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            dialogContent.setLayoutParams(params);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
            params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
            if (boolean_DialogGravityTop) {
                params2.topMargin = helper.getStatusBarHeight(mContext);
            }
            if (boolean_DialogGravityBottom) {
                if (!helper.isDeviceHorizontal(mContext)) {
                    params2.bottomMargin = helper.getNavigationBarSize(mContext).y;
                }
            }
            if (boolean_DialogGravityRight) {
                if (helper.isDeviceHorizontal(mContext)) {
                    params2.rightMargin = helper.getNavigationBarSize(mContext).x;
                }
            }
            frame.setLayoutParams(params2);
            frame3.setLayoutParams(params2);
        } else {
            FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
            crparams.width = ((int) helper.convertDpToPixel(340, mContext)) + (boolean_DialogGravityRight ? helper.getNavigationBarSize(mContext).x : 0);
            crparams.height = ((int) helper.convertDpToPixel(150, mContext)) + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));
            revealView.setLayoutParams(crparams);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
            params.width = ((int) helper.convertDpToPixel(340, mContext)) + (boolean_DialogGravityRight ? helper.getNavigationBarSize(mContext).x : 0);
            dialogContent.setLayoutParams(params);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
            params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
            if (boolean_DialogGravityTop) {
                params2.topMargin = helper.getStatusBarHeight(mContext);
            }
            if (boolean_DialogGravityBottom) {
                if (!helper.isDeviceHorizontal(mContext)) {
                    params2.bottomMargin = helper.getNavigationBarSize(mContext).y;
                }
            }
            if (boolean_DialogGravityRight) {
                if (helper.isDeviceHorizontal(mContext)) {
                    params2.rightMargin = helper.getNavigationBarSize(mContext).x;
                }
            }
            frame.setLayoutParams(params2);
            frame3.setLayoutParams(params2);
            //params2.height = (int) helper.convertDpToPixel(150f, mContext);
            //frameConfirm.setLayoutParams(params2);
        }
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(frame.getLayoutParams());
        params3.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params3.height = (int) helper.convertDpToPixel(150, mContext);
        if (boolean_DialogGravityTop) {
            params3.topMargin = helper.getStatusBarHeight(mContext);
        }
        if (boolean_DialogGravityBottom) {
            if (!helper.isDeviceHorizontal(mContext)) {
                params3.bottomMargin = helper.getNavigationBarSize(mContext).y;
            }
        }
        if (boolean_DialogGravityRight) {
            if (helper.isDeviceHorizontal(mContext)) {
                params3.rightMargin = helper.getNavigationBarSize(mContext).x;
            }
        }
        frame2.setLayoutParams(params3);
        frameConfirm.setLayoutParams(params3);

        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) < mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            LayoutTransition lt = new LayoutTransition();
            dialogMain.setLayoutTransition(lt);
            dialogContent.setLayoutTransition(lt);
        }

        dialogMain.setGravity(gravity);
        frameLinear.setGravity(gravity);
        frame2Linear.setGravity(Gravity.CENTER);
        frame3Linear.setGravity(gravity);
        frameConfirmLinear.setGravity(gravity);

        status = (TextView) view.findViewById(R.id.status);
        status_detail = (TextView) view.findViewById(R.id.status_detail);

        progressbg = (ImageView) view.findViewById(R.id.progressbg);
        progressbg.setVisibility((GraphicBehindProgress ? View.VISIBLE : View.INVISIBLE));
        progress = (ProgressBar) view.findViewById(R.id.progress);

        progress.getIndeterminateDrawable().setColorFilter(
                Color.parseColor("#ffffff"),
                android.graphics.PorterDuff.Mode.SRC_IN);

        if (orderPrefs.getAll().isEmpty()) {
            orderPrefs.edit().putInt("0_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();
            orderPrefs.edit().putString("0_item_title", "Shutdown").apply();
            orderPrefs.edit().putInt("1_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();
            orderPrefs.edit().putString("1_item_title", "Reboot").apply();
            orderPrefs.edit().putInt("2_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();
            orderPrefs.edit().putString("2_item_title", "SoftReboot").apply();
            orderPrefs.edit().putInt("3_item_type", visibilityOrder_ListAdapter.TYPE_MULTI).apply();
            orderPrefs.edit().putString("3_item1_title", "Recovery").apply();
            orderPrefs.edit().putString("3_item2_title", "Bootloader").apply();
            orderPrefs.edit().putString("3_item3_title", "SafeMode").apply();
        }

        types = new ArrayList<>();
        items = new ArrayList<>();
        names = new ArrayList<>();
        hideDesc = new ArrayList<>();
        hideOnLockscreen = new ArrayList<>();
        final ArrayList<String> MultiPage = new ArrayList<>();
        texts = new ArrayList<>();
        boolean firstItemDrawn = false;
        for (int i = 0; i < orderPrefs.getAll().size(); i++) {
            if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                names.add((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i);
                types.add(orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL));
                hideDesc.add(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                hideOnLockscreen.add(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                if (types.get(i) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                    items.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    texts.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""));
                    if (MultiPage.size() == 0 || (MultiPage.size() == 1 && !firstItemDrawn)) {
                        if (!mKeyguardShowing || (mKeyguardShowing && !hideOnLockscreen.get(i))) {
                            ListContainer.addView(createNormalItem(items.get(i), (MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) : null), hideDesc.get(i), texts.get(i)));
                        }
                        firstItemDrawn = true;
                    }
                } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_MULTI) {
                    items.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_title", "null") + "|" +
                            orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_title", "null") + "|" +
                            orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_title", "null"));
                    texts.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_text", "< default >") + "|" +
                            orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_text", "< default >") + "|" +
                            orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_text", "< default >"));
                    if (MultiPage.size() == 0) {
                        if (!mKeyguardShowing || (mKeyguardShowing && !hideOnLockscreen.get(i))) {
                            ListContainer.addView(createMultiItem(items.get(i), texts.get(i)));
                        }
                    }
                } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                    items.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    texts.add("");
                    MultiPage.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    if (!mKeyguardShowing || (mKeyguardShowing && !hideOnLockscreen.get(i))) {
                        firstItemDrawn = false;
                    }
                } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                    items.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    texts.add("");
                    if (MultiPage.size() > 0) MultiPage.remove(MultiPage.size() - 1);
                }
            }
        }

        mRun = new Runnable() {

            @Override
            public void run() {
                refreshIcons();
            }
        };
        mHandler.postDelayed(mRun, 250L);

        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            dialogMain.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
        }

        return view;

    }

    private View createNormalItem(String title, String pageItem, boolean hideDesc, String customText) {
        final String mTitle = title;
        View inflated = mInflater.inflate(R.layout.powermenu_normal, null, false);

        LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenunormal_root);
        ImageView icon = (ImageView) inflated.findViewById(R.id.powermenunormal_icon);
        ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenunormal_icon2);
        icon2.setVisibility(View.GONE);
        TextView text = (TextView) inflated.findViewById(R.id.powermenunormal_text1);
        TextView desc = (TextView) inflated.findViewById(R.id.powermenunormal_text2);
        desc.setVisibility(View.GONE);

        if (!title.equalsIgnoreCase("Empty")) {
            String string = title;
            if (customText.isEmpty()) {
                if (string.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + title, "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + title, "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
            } else {
                string = customText;
            }
            text.setText(string);
            text.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));

            if (!hideDesc) {
                String descString = title;
                try {
                    descString = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + title + "Desc", "string", MainActivity.class.getPackage().getName()));
                    desc.setVisibility(View.VISIBLE);
                } catch (Throwable t) {
                    try {
                        descString = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + title + "Desc", "string", MainActivity.class.getPackage().getName()));
                        desc.setVisibility(View.VISIBLE);
                    } catch (Throwable t1) {
                    }
                }
                if (preferences.getLong("ScreenshotDelay", 1000) == 0) {
                    descString = descString.replace("[SCREENSHOTDELAY]", mContext.getString(R.string.advancedPrefs_DelayZero));
                } else {
                    descString = descString.replace("[SCREENSHOTDELAY]", helper.getTimeString(preferences.getLong("ScreenshotDelay", 1000), true));
                }
                descString = descString.replace("[AUTOOFF]", helper.getTimeString(preferences.getLong("FlashlightAutoOff", 1000 * 60 * 10), true));
                String descText = mContext.getString(R.string.SoundMode_Normal);
                if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                    descText = mContext.getString(R.string.SoundMode_Silent);
                } else if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                    descText = mContext.getString(R.string.SoundMode_Vibrate);
                }
                if (descString.contains("[SOUNDMODE]")) {
                    soundModeIcon_Text.add(desc);
                }
                descString = descString.replace("[SOUNDMODE]", descText);
                desc.setText(descString);
            }

            desc.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));

            createCircleIcon(icon, icon2, title, string, colorPrefs.getString("Dialog" + (title.contains(".") ? "AppShortcut" : title) + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + (title.contains(".") ? "AppShortcut" : title) + "_Textcolor", "#ffffff"));

            if (pageItem == null || pageItem.isEmpty()) {
                root.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        performMenuClick(mTitle, p1);
                    }
                });
            } else {
                final String thisGroup = pageItem;
                root.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        performMenuClick("multipage:" + thisGroup, p1);
                    }
                });
            }
        } else {
            root.setVisibility(View.INVISIBLE);
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[10][1].toString(), PreferencesAnimationsFragment.defaultTypes[10]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            root.startAnimation(helper.getAnimation(mContext, animationPrefs, 9, false));
        }
        return inflated;
    }

    private View createMultiItem(String title, String customText) {
        View inflated = mInflater.inflate(R.layout.powermenu_multi, null, false);

        final String[] titles = title.split("\\|");
        LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item1);
        ImageView icon = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon);
        ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon2);
        icon2.setVisibility(View.GONE);
        TextView text = (TextView) inflated.findViewById(R.id.powermenumulti_item1text);

        if (!titles[0].equalsIgnoreCase("Empty")) {
            String string = "";
            if (!customText.split("\\|")[0].equalsIgnoreCase("< default >")) {
                string = customText.split("\\|")[0];
            }
            if (string.isEmpty()) {
                string = titles[0];
                if (string.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + titles[0], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + titles[0], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            string = "Failed to get String resource for powerMenuBottom_" + titles[0];
                            Log.e("NPM", "Failed to get String resource for powerMenuBottom_" + titles[0], t);
                        }
                    }
                }
            }
            text.setText(string);
            text.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
            if (titles[0].equalsIgnoreCase("SoundMode")) {
                soundModeIcon_Text.add(null);
            }

            createCircleIcon(icon, icon2, titles[0], string, colorPrefs.getString("Dialog" + (titles[0].contains(".") ? "AppShortcut" : titles[0]) + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + (titles[0].contains(".") ? "AppShortcut" : titles[0]) + "_Textcolor", "#ffffff"));

            root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    performMenuClick(titles[0], p1);
                }
            });
        } else {
            root.setVisibility(View.GONE);
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            root.startAnimation(helper.getAnimation(mContext, animationPrefs, 12, false));
        }

        LinearLayout root2 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item2);
        ImageView iconitem2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon);
        ImageView icon2item2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon2);
        icon2item2.setVisibility(View.GONE);
        TextView text2 = (TextView) inflated.findViewById(R.id.powermenumulti_item2text);
        if (!titles[1].equalsIgnoreCase("Empty")) {
            String string2 = "";
            if (customText.split("\\|").length >= 2 && !customText.split("\\|")[1].equalsIgnoreCase("< default >")) {
                string2 = customText.split("\\|")[1];
            }
            if (string2.isEmpty()) {
                string2 = titles[1];
                if (string2.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string2 = pm.getApplicationInfo(string2.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + titles[1], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + titles[1], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            Log.e("NPM", "Failed to get String resource for powerMenuBottom_" + titles[1], t);
                        }
                    }
                }
            }
            text2.setText(string2);
            text2.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
            if (titles[1].equalsIgnoreCase("SoundMode")) {
                soundModeIcon_Text.add(null);
            }

            createCircleIcon(iconitem2, icon2item2, titles[1], string2, colorPrefs.getString("Dialog" + (titles[1].contains(".") ? "AppShortcut" : titles[1]) + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + (titles[1].contains(".") ? "AppShortcut" : titles[1]) + "_Textcolor", "#ffffff"));

            root2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    performMenuClick(titles[1], p1);
                }
            });
        } else {
            root2.setVisibility(View.GONE);
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            Animation anim = helper.getAnimation(mContext, animationPrefs, 12, false);
            anim.setStartOffset((anim.getDuration() / 30) * 3);
            root2.startAnimation(anim);
        }

        LinearLayout root3 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item3);
        ImageView iconitem3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon);
        ImageView icon2item3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon2);
        icon2item3.setVisibility(View.GONE);
        TextView text3 = (TextView) inflated.findViewById(R.id.powermenumulti_item3text);

        if (!titles[2].equalsIgnoreCase("Empty")) {
            String string3 = "";
            if (customText.split("\\|").length == 3 && !customText.split("\\|")[2].equalsIgnoreCase("< default >")) {
                string3 = customText.split("\\|")[2];
            }
            if (string3.isEmpty()) {
                string3 = titles[2];
                if (string3.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string3 = pm.getApplicationInfo(string3.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + titles[2], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + titles[2], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            Log.e("NPM", "Failed to get String resource for powerMenuBottom_" + titles[2], t);
                        }
                    }
                }
            }
            text3.setText(string3);
            text3.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
            if (titles[2].equalsIgnoreCase("SoundMode")) {
                soundModeIcon_Text.add(null);
            }

            createCircleIcon(iconitem3, icon2item3, titles[2], string3, colorPrefs.getString("Dialog" + (titles[2].contains(".") ? "AppShortcut" : titles[2]) + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + (titles[2].contains(".") ? "AppShortcut" : titles[2]) + "_Textcolor", "#ffffff"));

            root3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    performMenuClick(titles[2], p1);
                }
            });
        } else {
            root3.setVisibility(View.GONE);
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            Animation anim = helper.getAnimation(mContext, animationPrefs, 12, false);
            anim.setStartOffset((anim.getDuration() / 30) * 6);
            root3.startAnimation(anim);
        }

        return inflated;
    }

    public void refreshIcons() {
        if (isAdded()) {
            if (!soundModeIcon_Image.isEmpty() && amRingerMode != am.getRingerMode()) {
                amRingerMode = am.getRingerMode();
                for (int i = 0; i < soundModeIcon_Image.size(); i++) {
                    if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                        loadImage(soundModeIcon_Image.get(i), 14, soundModeIcon_Color.get(i));
                    } else if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        loadImage(soundModeIcon_Image.get(i), 13, soundModeIcon_Color.get(i));
                    } else {
                        loadImage(soundModeIcon_Image.get(i), 12, soundModeIcon_Color.get(i));
                    }
                }
                if (!soundModeIcon_Text.isEmpty()) {
                    for (int i = 0; i < soundModeIcon_Text.size(); i++) {
                        if (soundModeIcon_Text.get(i) != null) {
                            String descText = getString(R.string.SoundMode_Normal);
                            if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                                descText = getString(R.string.SoundMode_Silent);
                            } else if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                                descText = getString(R.string.SoundMode_Vibrate);
                            }
                            String descString = mContext.getString(R.string.powerMenuMain_SoundModeDesc).replace("[SOUNDMODE]", descText);
                            soundModeIcon_Text.get(i).setText(descString);
                        }
                    }
                }
            }

            if (!airplaneModeIcon_Image.isEmpty()) {
                for (int i = 0; i < airplaneModeIcon_Image.size(); i++) {
                    if (Build.VERSION.SDK_INT >= 17) {
                        try {
                            if (airplaneMode != android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON)) {
                                try {
                                    if (android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0) {
                                        loadImage(airplaneModeIcon_Image.get(i), 10, airplaneModeIcon_Color.get(i));
                                    } else {
                                        loadImage(airplaneModeIcon_Image.get(i), 9, airplaneModeIcon_Color.get(i));
                                    }
                                } catch (Throwable e) {
                                    loadImage(airplaneModeIcon_Image.get(i), 10, airplaneModeIcon_Color.get(i));
                                }
                                airplaneMode = android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON);
                            }
                        } catch (Throwable e) {
                        }
                    } else {
                        try {
                            if (airplaneMode != android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON)) {
                                try {
                                    if (android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON) == 0) {
                                        loadImage(airplaneModeIcon_Image.get(i), 10, airplaneModeIcon_Color.get(i));
                                    } else {
                                        loadImage(airplaneModeIcon_Image.get(i), 9, airplaneModeIcon_Color.get(i));
                                    }
                                } catch (Throwable e) {
                                    loadImage(airplaneModeIcon_Image.get(i), 10, airplaneModeIcon_Color.get(i));
                                }
                                airplaneMode = android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON);
                            }
                        } catch (Throwable e) {
                        }
                    }
                }
            }

            if (!flashlightIcon_Image.isEmpty()) {
                for (int i = 0; i < flashlightIcon_Image.size(); i++) {
                    try {
                        if (flashlightOn != (TorchService.getTorchState() == TorchService.TORCH_STATUS_ON)) {
                            if (TorchService.getTorchState() == TorchService.TORCH_STATUS_ON) {
                                loadImage(flashlightIcon_Image.get(i), 6, flashlightIcon_Color.get(i));
                            } else {
                                loadImage(flashlightIcon_Image.get(i), 7, flashlightIcon_Color.get(i));
                            }
                            flashlightOn = TorchService.getTorchState() == TorchService.TORCH_STATUS_ON;
                        }
                    } catch (Throwable t) {
                    }
                }
            }

            if (!rotateIcon_Image.isEmpty()) {
                for (int i = 0; i < flashlightIcon_Image.size(); i++) {
                    try {
                        if (rotate != android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION)) {
                            try {
                                if (android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                                    loadImage(rotateIcon_Image.get(i), 20, rotateIcon_Color.get(i));
                                } else {
                                    loadImage(rotateIcon_Image.get(i), 21, rotateIcon_Color.get(i));
                                }
                            } catch (Throwable e) {
                                loadImage(rotateIcon_Image.get(i), 21, rotateIcon_Color.get(i));
                            }
                            rotate = android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                        }
                    } catch (Throwable e) {
                    }
                }
            }

            if (!playPauseIcon_Image.isEmpty()) {
                for (int i = 0; i < playPauseIcon_Image.size(); i++) {
                    try {
                        if (mediaPlaying != am.isMusicActive()) {
                            try {
                                if (!am.isMusicActive()) {
                                    loadImage(playPauseIcon_Image.get(i), 23, playPauseIcon_Color.get(i));
                                } else {
                                    loadImage(playPauseIcon_Image.get(i), 24, playPauseIcon_Color.get(i));
                                }
                            } catch (Throwable e) {
                                loadImage(playPauseIcon_Image.get(i), 23, playPauseIcon_Color.get(i));
                            }
                            mediaPlaying = am.isMusicActive();
                        }
                    } catch (Throwable e) {
                    }
                }
            }
            mHandler.postDelayed(mRun, 250L);
        }
    }

    public void createCircleIcon(ImageView background, final ImageView foreground, String text, String finalText, String color1, String color2) {
        try {
            if (preferences.getBoolean("UseGraphics", false)) {
                GraphicDrawable drawable = GraphicDrawable.builder().buildRound((Bitmap) null, Color.parseColor(color1));
                background.setImageDrawable(drawable);
                foreground.setVisibility(View.VISIBLE);
                if (text.equalsIgnoreCase("Shutdown")) {
                    loadImage(foreground, 1, color2);
                } else if (text.equalsIgnoreCase("Reboot")) {
                    loadImage(foreground, 2, color2);
                } else if (text.equalsIgnoreCase("SoftReboot")) {
                    loadImage(foreground, 3, color2);
                } else if (text.equalsIgnoreCase("Screenshot")) {
                    loadImage(foreground, 4, color2);
                } else if (text.equalsIgnoreCase("Screenrecord")) {
                    loadImage(foreground, 5, color2);
                } else if (text.equalsIgnoreCase("Flashligth")) {
                    if (TorchService.getTorchState() == TorchService.TORCH_STATUS_OFF) {
                        loadImage(foreground, 7, color2);
                    } else {
                        loadImage(foreground, 6, color2);
                    }
                    flashlightIcon_Image.add(foreground);
                    flashlightIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("ExpandedDesktop")) {
                    loadImage(foreground, 8, color2);
                } else if (text.equalsIgnoreCase("AirplaneMode")) {
                    if (Build.VERSION.SDK_INT >= 17) {
                        try {
                            if (android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0) {
                                loadImage(foreground, 10, color2);
                            } else {
                                loadImage(foreground, 9, color2);
                            }
                        } catch (Throwable e) {
                            loadImage(foreground, 10, color2);
                        }
                    } else {
                        try {
                            if (android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON) == 0) {
                                loadImage(foreground, 10, color2);
                            } else {
                                loadImage(foreground, 9, color2);
                            }
                        } catch (Throwable e) {
                            loadImage(foreground, 10, color2);
                        }
                    }
                    airplaneModeIcon_Image.add(foreground);
                    airplaneModeIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("RestartUI")) {
                    loadImage(foreground, 11, color2);
                } else if (text.equalsIgnoreCase("SoundMode")) {
                    if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                        loadImage(foreground, 14, color2);
                    } else if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        loadImage(foreground, 13, color2);
                    } else {
                        loadImage(foreground, 12, color2);
                    }
                    soundModeIcon_Image.add(foreground);
                    soundModeIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("Recovery")) {
                    loadImage(foreground, 15, color2);
                } else if (text.equalsIgnoreCase("Bootloader")) {
                    loadImage(foreground, 16, color2);
                } else if (text.equalsIgnoreCase("SafeMode")) {
                    loadImage(foreground, 17, color2);
                } else if (text.equalsIgnoreCase("SoundVibrate")) {
                    loadImage(foreground, 14, color2);
                } else if (text.equalsIgnoreCase("SoundNormal")) {
                    loadImage(foreground, 12, color2);
                } else if (text.equalsIgnoreCase("SoundSilent")) {
                    loadImage(foreground, 13, color2);
                } else if (text.equalsIgnoreCase("KillApp")) {
                    loadImage(foreground, 18, color2);
                } else if (text.contains(".")) {
                    if (mLoadAppIcons) {
                        try {
                            Drawable d = pm.getApplicationIcon(text.split("/")[0]);
                            foreground.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
                            foreground.setImageDrawable(d);
                            if (mColorizeNonStockIcons) {
                                foreground.setColorFilter(Color.parseColor(color2),
                                        android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            foreground.setVisibility(View.VISIBLE);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                foreground.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            foreground.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        loadImage(foreground, 19, color2);
                    }
                } else if (text.equalsIgnoreCase("ToggleRotate")) {
                    try {
                        if (android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                            loadImage(foreground, 20, color2);
                        } else {
                            loadImage(foreground, 21, color2);
                        }
                    } catch (Throwable e) {
                        loadImage(foreground, 21, color2);
                    }
                    rotateIcon_Image.add(foreground);
                    rotateIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("MediaPrevious")) {
                    loadImage(foreground, 22, color2);
                } else if (text.equalsIgnoreCase("MediaPlayPause")) {
                    try {
                        if (!mediaPlaying) {
                            loadImage(foreground, 23, color2);
                        } else {
                            loadImage(foreground, 24, color2);
                        }
                    } catch (Throwable t) {
                        loadImage(foreground, 23, color2);
                    }
                    playPauseIcon_Image.add(foreground);
                    playPauseIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("MediaNext")) {
                    loadImage(foreground, 25, color2);
                }
            } else {
                if (text.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        text = pm.getApplicationInfo(text.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                }
                TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig()
                        .buildRound(finalText.substring(0, 1), Color.parseColor(color1));
                background.setImageDrawable(drawable);
                foreground.setVisibility(View.GONE);
            }
        } catch (Throwable t) {
            Log.e("NPM", "Failed to create Circle Icon.", t);
        }
    }

    private void loadImage(final ImageView image, final int id, final String color) {
        if (new File(mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[id][2] + ".png").exists()) {
            mImageLoader.displayImage("file://" + mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[id][2] + ".png",
                    image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            image.setImageBitmap(null);
                            image.setPadding((int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext));
                            image.setColorFilter(Color.parseColor("#ffffff"),
                                    android.graphics.PorterDuff.Mode.DST);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, true));
                            }
                            image.setVisibility(View.INVISIBLE);
                            super.onLoadingStarted(imageUri, view);
                        }

                        @Override
                        public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                            image.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
                            image.setImageBitmap(loadedImage);
                            if (mColorizeNonStockIcons) {
                                image.setColorFilter(Color.parseColor(color),
                                        android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            image.setVisibility(View.VISIBLE);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Log.e("NPM:xposedDialog", "Failed to load image '" + imageUri + "': " + failReason.getCause());
                            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.defaultGraphics[id][1]));
                            image.setColorFilter(Color.parseColor(color),
                                    android.graphics.PorterDuff.Mode.MULTIPLY);
                            image.setVisibility(View.VISIBLE);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                            }
                        }
                    });
        } else {
            image.setPadding((int) helper.convertDpToPixel(5, mContext) + (int) mGraphicsPadding, (int) helper.convertDpToPixel(5, mContext) + (int) mGraphicsPadding, (int) helper.convertDpToPixel(5, mContext) + (int) mGraphicsPadding, (int) helper.convertDpToPixel(5, mContext) + (int) mGraphicsPadding);
            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.defaultGraphics[id][1]));
            image.setColorFilter(Color.parseColor(color),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
            image.setVisibility(View.VISIBLE);
            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
            }
        }
    }

    private void performMenuClick(final String name, final View v) {
        if (name.contains("multipage:")) {
            if (frame3.getVisibility() == View.GONE) {
                ListContainer2.removeAllViews();
            } else {
                ListContainer.removeAllViews();
            }

            String page = name.split("\\:")[1];
            if (!page.equalsIgnoreCase("Root")) {
                if (confirmDialog.isEmpty()) SubDialogs.add(page);
                boolean firstItemDrawn = false;
                boolean inRightSpot = false;
                ArrayList<String> MultiPage = new ArrayList<String>();
                //Log.i("NPM:itemLoader","Searching for multi item with the code "+page);
                for (int i = 0; i < items.size(); i++) {
                    if (types.get(i) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                        if (items.get(i).equals(page)) {
                            firstItemDrawn = false;
                            //MultiPage.add(items.get(i));
                            inRightSpot = true;
                            //Log.i("NPM:itemLoader","Got the right spot!");
                        } else {
                            if (inRightSpot && MultiPage.size() == 0) {
                                firstItemDrawn = false;
                                MultiPage.add(items.get(i));
                                page = items.get(i);
                            }
                            //Log.i("NPM:itemLoader","Found multi item with another code"+items.get(i)+(inRightSpot ? "" : " but ignoring."));
                        }
                    } else if (types.get(i) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                        if (items.get(i).equals(page)) {
                            inRightSpot = false;
                            if (MultiPage.size() > 0) {
                                //page = MultiPage.get(MultiPage.size()-1);
                                MultiPage.remove(MultiPage.size() - 1);
                            }
                            //Log.i("NPM:itemLoader","Left the right spot.");
                        }
                    } else if (inRightSpot) {
                        if (types.get(i) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            if (names.get(i).contains(page)) {
                                if (MultiPage.size() == 0 || !firstItemDrawn) {
                                    if (!mKeyguardShowing || (mKeyguardShowing && !hideOnLockscreen.get(i))) {
                                        if (frame3.getVisibility() == View.GONE) {
                                            ListContainer2.addView(createNormalItem(items.get(i), (MultiPage.size() > 0 ? page : null), hideDesc.get(i), texts.get(i)));
                                        } else {
                                            ListContainer.addView(createNormalItem(items.get(i), (MultiPage.size() > 0 ? page : null), hideDesc.get(i), texts.get(i)));
                                        }
                                    }
                                }
                                if (!firstItemDrawn) {
                                    firstItemDrawn = true;
                                    if (MultiPage.size() > 0) {
                                        page = MultiPage.get(MultiPage.size() - 1);
                                        //MultiPage.remove(MultiPage.size()-1);
                                    }
                                }
                                //Log.i("NPM:itemLoader","Added "+items.get(i)+" in "+page);
                                //if(MultiPage.size()>0) MultiPage.remove(MultiPage.size()-1);
                            }
                        } else if (types.get(i) == visibilityOrder_ListAdapter.TYPE_MULTI) {
                            if (MultiPage.size() == 0 && names.get(i).contains(page)) {
                                if (!mKeyguardShowing || (mKeyguardShowing && !hideOnLockscreen.get(i))) {
                                    if (frame3.getVisibility() == View.GONE) {
                                        ListContainer2.addView(createMultiItem(items.get(i), texts.get(i)));
                                    } else {
                                        ListContainer.addView(createMultiItem(items.get(i), texts.get(i)));
                                    }
                                }
                                //Log.i("NPM:itemLoader","Added "+items.get(i) +" in "+page);
                            }
                        }
                    }
                }
            } else {
                final ArrayList<String> MultiPage = new ArrayList<String>();
                boolean firstItemDrawn = false;
                for (int i = 0; i < orderPrefs.getAll().size(); i++) {
                    if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                        if (types.get(i) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            if (MultiPage.size() == 0 || (MultiPage.size() == 1 && !firstItemDrawn)) {
                                if (!mKeyguardShowing || (mKeyguardShowing && !hideOnLockscreen.get(i))) {
                                    if (frame3.getVisibility() == View.GONE) {
                                        ListContainer2.addView(createNormalItem(items.get(i), (MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) : null), hideDesc.get(i), texts.get(i)));
                                    } else {
                                        ListContainer.addView(createNormalItem(items.get(i), (MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) : null), hideDesc.get(i), texts.get(i)));
                                    }
                                }
                                firstItemDrawn = true;
                            }
                        } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_MULTI) {
                            if (MultiPage.size() == 0) {
                                if (!mKeyguardShowing || (mKeyguardShowing && !hideOnLockscreen.get(i))) {
                                    if (frame3.getVisibility() == View.GONE) {
                                        ListContainer2.addView(createMultiItem(items.get(i), texts.get(i)));
                                    } else {
                                        ListContainer.addView(createMultiItem(items.get(i), texts.get(i)));
                                    }
                                }
                            }
                        } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                            MultiPage.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                            firstItemDrawn = false;
                        } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                            if (MultiPage.size() > 0) MultiPage.remove(MultiPage.size() - 1);
                        }
                    }
                }
            }
            if (frame3.getVisibility() == View.GONE) {
                frame3.setVisibility(View.VISIBLE);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    if (confirmDialog.isEmpty()) {
                        frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                    } else {
                        confirmDialog = "";
                    }
                    frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                }
                frame.setVisibility(View.GONE);
            } else {
                frame.setVisibility(View.VISIBLE);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                    frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                }
                frame3.setVisibility(View.GONE);
            }
        } else {
            if (RequireConfirmation && (name.equalsIgnoreCase("Shutdown") || name.equalsIgnoreCase("Reboot") || name.equalsIgnoreCase("SoftReboot") || name.equalsIgnoreCase("Recovery") || name.equalsIgnoreCase("Bootloader") || name.equalsIgnoreCase("SafeMode"))) {
                if (!confirmDialog.equalsIgnoreCase(name)) {
                    confirmDialog = name;
                    SubDialogs.add("Confirm");
                    confirmAction.setText(mContext.getString(R.string.powerMenu_SureToRebootPowerOff).split("\\|")[(name.equalsIgnoreCase("Shutdown") ? 1 : 0)]);
                    confirmNo.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_NO]);
                    confirmNo.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {

                            dismissThis();
                        }
                    });
                    confirmYes.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_YES]);
                    confirmYes.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {

                            performMenuClick(name, v);
                        }
                    });
                    if (frame3.getVisibility() == View.GONE) {
                        frameConfirm.setVisibility(View.VISIBLE);
                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                        }
                        frame.setVisibility(View.GONE);
                    } else {
                        frameConfirm.setVisibility(View.VISIBLE);
                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                        }
                        frame3.setVisibility(View.GONE);
                    }
                    return;
                }
            }
            SubDialogs.clear();
            if (name.equalsIgnoreCase("Shutdown")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogShutdown_Backgroundcolor", "#d32f2f"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop();
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_Shutdown);
                status_detail.setText(R.string.powerMenu_Shuttingdown);

                setProgressScreen("Shutdown");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doShutdown(mContext, 0);
                    } else if (UseRootCommands) {
                        new BackgroundThread(SHUTDOWN).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_SHUTDOWN);
                        mContext.sendBroadcast(launchIntent);
                    }
                }
            } else if (name.equalsIgnoreCase("Reboot")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogReboot_Backgroundcolor", "#3f51b5"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop();
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_Reboot);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("Reboot");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 0);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_REBOOT);
                        mContext.sendBroadcast(launchIntent);
                    }
                }
            } else if (name.equalsIgnoreCase("SoftReboot")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogSoftReboot_Backgroundcolor", "#e91e63"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop();
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_SoftReboot);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("SoftReboot");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 1);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_SOFT_REBOOT_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_SOFTREBOOT);
                        mContext.sendBroadcast(launchIntent);
                    }
                }
            } else if (name.equalsIgnoreCase("Screenshot")) {
                if (!mPreviewMode) {
                    dismissThis();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            Intent takeScreenshotBC = new Intent();
                            takeScreenshotBC.setAction(XposedMain.NPM_ACTION_BROADCAST_SCREENSHOT);
                            mContext.sendBroadcast(takeScreenshotBC);
                            //Toast.makeText(mContext, "Taking screenshot...",Toast.LENGTH_SHORT).show();
                        }
                    }, preferences.getLong("ScreenshotDelay", 1000));
                }
            } else if (name.equalsIgnoreCase("Screenrecord")) {
                if (!mPreviewMode) {
                    dismissThis();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            Intent takeScreenrecordBC = new Intent();
                            takeScreenrecordBC.setAction(XposedMain.NPM_ACTION_BROADCAST_SCREENRECORD);
                            mContext.sendBroadcast(takeScreenrecordBC);
                            //Toast.makeText(mContext, "Taking screenrecord...",Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                }
            } else if (name.equalsIgnoreCase("Flashlight")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    final Handler handler = new Handler();
                    new Thread() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    toggleTorch(false);
                                }
                            });
                        }
                    }.start();
                }
            } else if (name.equalsIgnoreCase("ExpandedDesktop")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    Intent launchIntent = new Intent("gravitybox.intent.action.TOGGLE_EXPANDED_DESKTOP");
                    mContext.sendBroadcast(launchIntent);
                }
            } else if (name.equalsIgnoreCase("AirplaneMode")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
                    mContext.sendBroadcast(launchIntent);
                }
            } else if (name.equalsIgnoreCase("RestartUI")) {
                if (!mPreviewMode) {
                    dismissThis();
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_KILLSYSTEMUI);
                    mContext.sendBroadcast(launchIntent);
                }
            } else if (name.equalsIgnoreCase("SoundMode")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    if (amRingerMode == AudioManager.RINGER_MODE_NORMAL) {
                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    } else if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    } else if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            } else if (name.equalsIgnoreCase("Recovery")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogRecovery_Backgroundcolor", "#8bc34a"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop();
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_Recovery);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("Recovery");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 2);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_RECOVERY_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_REBOOTRECOVERY);
                        mContext.sendBroadcast(launchIntent);
                    }
                }
            } else if (name.equalsIgnoreCase("Bootloader")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogBootloader_Backgroundcolor", "#277b71"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop();
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_Bootloader);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("Bootloader");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 3);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_BOOTLOADER_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_REBOOTBOOTLOADER);
                        mContext.sendBroadcast(launchIntent);
                    }
                }
            } else if (name.equalsIgnoreCase("SafeMode")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogSafeMode_Backgroundcolor", "#009688"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop();
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_SafeMode);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("SafeMode");
                if (!mPreviewMode) {
                    new BackgroundThread(REBOOT_SAFE_MODE).start();
                }
            } else if (name.equalsIgnoreCase("SoundVibrate")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
            } else if (name.equalsIgnoreCase("SoundNormal")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            } else if (name.equalsIgnoreCase("SoundSilent")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            } else if (name.contains(".")) {
                if (!mPreviewMode) {
                    dismissThis();
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(name.split("/")[0], name.split("/")[1]));
                        mContext.startActivity(intent);
                    } catch (Throwable e) {
                        Log.e("NPM:xposedDialog", "No package with name '" + name + "' found...", e);
                    }
                }
            } else if (name.equalsIgnoreCase("ActivityShortcut")) {
                if (!mPreviewMode) {
                    dismissThis();

                }
            } else if (name.equalsIgnoreCase("KillApp")) {
                if (!mPreviewMode) {
                    dismissThis();
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_KILLAPP);
                    mContext.sendBroadcast(launchIntent);
                }
            } else if (name.equalsIgnoreCase("ToggleRotate")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEROTATION);
                    mContext.sendBroadcast(launchIntent);
                }
            } else if (name.equalsIgnoreCase("MediaPrevious")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    long eventtime = SystemClock.uptimeMillis();
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    mContext.sendOrderedBroadcast(downIntent, null);
                }
            } else if (name.equalsIgnoreCase("MediaPlayPause")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    long eventtime = SystemClock.uptimeMillis();
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    mContext.sendOrderedBroadcast(downIntent, null);
                }
            } else if (name.equalsIgnoreCase("MediaNext")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) dismissThis();
                    long eventtime = SystemClock.uptimeMillis();
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    mContext.sendOrderedBroadcast(downIntent, null);
                }
            }
        }
    }

    public void setProgressScreen(final String showingFor) {
        status.setTextColor(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")));
        status_detail.setTextColor(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")));

        //if(GraphicBehindProgress) loadImage(progressbg, 0, colorPrefs.getString("DialogShutdown_Textcolor", "#ffffff"));
        if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("file")) {
            if (new File(mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[0][2] + ".png").exists()) {
                mImageLoader.displayImage("file://" + mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[0][2] + ".png",
                        progressbg, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                progress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                                progressbg.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
                                progressbg.setVisibility(View.VISIBLE);
                                progressbg.setImageBitmap(loadedImage);
                                if (mColorizeNonStockIcons) {
                                    progressbg.setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")),
                                            android.graphics.PorterDuff.Mode.MULTIPLY);
                                }
                                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                    Animation blendIn = helper.getAnimation(mContext, animationPrefs, 6, false);
                                    blendIn.setAnimationListener(new Animation.AnimationListener() {

                                        @Override
                                        public void onAnimationEnd(Animation p1) {

                                            Animation progressAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_right);
                                            progressAnim.setRepeatMode(Animation.RESTART);
                                            progressAnim.setRepeatCount(Animation.INFINITE);
                                            progressbg.startAnimation(progressAnim);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation p1) {

                                        }

                                        @Override
                                        public void onAnimationStart(Animation p1) {

                                        }
                                    });
                                    progressbg.startAnimation(blendIn);
                                } else {
                                    Animation progressAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_right);
                                    progressAnim.setRepeatMode(Animation.RESTART);
                                    progressAnim.setRepeatCount(Animation.INFINITE);
                                    progressbg.startAnimation(progressAnim);
                                }
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                Log.e("NPM:xposedDialog", "Failed to load image '" + imageUri + "': " + failReason.getCause().toString());
                                progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                                progress.setVisibility(View.VISIBLE);
                                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                    progress.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                                }
                            }
                        });
            }
        } else {
            if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("Stock")) {
                progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    progress.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                }
            } else {
                progress.setVisibility(View.GONE);
                progressbg.setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                new loadProgressResource().execute(showingFor);
            }
        }
    }

    public void setHost(XposedMainActivity host) {
        this.menuHost = host;
    }

    public void setConfiguration(boolean previewMode, boolean hideOnClick, boolean keyguardShowing, boolean loadAppIcons, String styleName, float graphicPadding, boolean colorizeNonStockIcons, ImageLoader imageLoader, boolean deepXposedLogging) {
        this.mPreviewMode = previewMode;
        this.mHideOnClick = hideOnClick;
        this.mKeyguardShowing = keyguardShowing;
        this.mLoadAppIcons = loadAppIcons;
        this.sStyleName = styleName;
        this.mGraphicsPadding = graphicPadding;
        this.mColorizeNonStockIcons = colorizeNonStockIcons;
        this.mImageLoader = imageLoader;
        this.mDeepXposedLogging = deepXposedLogging;
    }

    public void setPreferences(SharedPreferences preferences, SharedPreferences colorPrefs, SharedPreferences orderPrefs, SharedPreferences animationPrefs) {
        this.preferences = preferences;
        this.colorPrefs = colorPrefs;
        this.orderPrefs = orderPrefs;
        this.animationPrefs = animationPrefs;
    }

    class loadProgressResource extends AsyncTask<Object, String, String> {

        Drawable image = null;
        String showingFor = "";

        @Override
        protected String doInBackground(Object[] p1) {

            showingFor = p1[0].toString();
            try {
                if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("pb/dr")) {
                    image = mContext.getResources().getDrawable(R.drawable.progress_pitchblack_darkred_cm13);
                } else if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("WeaReOne")) {
                    image = mContext.getResources().getDrawable(R.drawable.progress_weareone);
                }
            } catch (OutOfMemoryError t) {
                return t.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String p1) {

            super.onPostExecute(p1);
            if (p1 == null) {
                progressbg.setImageDrawable(image);
                progressbg.setVisibility(View.VISIBLE);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    progressbg.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                }
                ((AnimationDrawable) progressbg.getDrawable()).start();
            } else {
                Log.e("NPM:lPR", "Failed to load progress drawable: " + p1);
                progressbg.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
                progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    progress.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                }
            }
        }

    }

    private void setThreadPrio(int prio) {
        android.os.Process.setThreadPriority(prio);
    }

    private class BackgroundThread extends Thread {
        private Object sCmd;

        private BackgroundThread(Object cmd) {
            this.sCmd = cmd;
            if (mDeepXposedLogging)
                XposedUtils.log("Starting background task with command: " + cmd);
        }

        @Override
        public void run() {
            super.run();
            if (!mPreviewMode) {
                setThreadPrio(BG_PRIO);

                if (sCmd == null) {
                    return;
                }

                if (mDeepXposedLogging)
                    XposedUtils.log("Sending " + SHUTDOWN_BROADCAST);
                /**
                 * Sending a system broadcast to notify apps and the system that we're going down
                 * so that they write any outstanding data that might need to be flushed
                 */
                Shell.SU.run(SHUTDOWN_BROADCAST);


                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sCmd instanceof String) {
                            if (sCmd.toString().equalsIgnoreCase(SHUTDOWN + "notusedjet")) {
                                Intent broadcast = new Intent();
                                broadcast.setAction("de.NeonSoft.neopowermenu.Broadcast.poweroff");
                                mContext.sendBroadcast(broadcast);
                            } else {
                                if (mDeepXposedLogging)
                                    XposedUtils.log("Running su command " + sCmd.toString());
                                Shell.SU.run((String) sCmd);
                            }
                        } else if (sCmd instanceof String[]) {
                            if (mDeepXposedLogging)
                                XposedUtils.log("Running su commands " + sCmd);
                            Shell.SU.run((String[]) sCmd);
                        }
                    }
                }, RUNNABLE_DELAY_MS);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getShowsDialog()) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            //windowParams.type = windowParams.TYPE_KEYGUARD_DIALOG;

            window.setAttributes(windowParams);
        }
    }

    public void dismissThis() {
        if (SubDialogs.isEmpty()) {
            if (canDismiss || mPreviewMode) {
                if (!isDismissing) {
                    isDismissing = true;
                    int speed = 0;
                    if (mContext != null && animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                        Animation anim = helper.getAnimation(mContext, animationPrefs, 3, true);
                        dialogMain.startAnimation(anim);
                        speed = (int) anim.getDuration();
                    }
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            if (dialogMain != null) dialogMain.setVisibility(View.GONE);
                            menuHost.dismissThis();
                        }
                    }, Math.max(speed - 100, 0));
                }
            }
        } else {
            if (!confirmDialog.isEmpty()) {
                frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                frameConfirm.setVisibility(View.GONE);
            }
            SubDialogs.remove(SubDialogs.size() - 1);
            String parent = "root";
            if (!SubDialogs.isEmpty()) {
                parent = SubDialogs.get(SubDialogs.size() - 1);
            }
            if(menuHost.DeepXposedLogging) {
                Log.i("NPM:dT", "Total entries: " + SubDialogs.size());
                Log.i("NPM:dT", "Performing menu back to: " + parent);
            }
            performMenuClick("multipage:" + parent, null);
        }
    }

    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        if (getShowsDialog()) {
            int gravity = 0;
            if (boolean_DialogGravityTop) {
                gravity |= Gravity.TOP;
            } else if (boolean_DialogGravityBottom) {
                gravity |= Gravity.BOTTOM;
            } else {
                gravity |= Gravity.CENTER_VERTICAL;
            }
            if (boolean_DialogGravityLeft) {
                gravity |= Gravity.LEFT;
            } else if (boolean_DialogGravityRight) {
                gravity |= Gravity.RIGHT;
            } else {
                gravity |= Gravity.CENTER_HORIZONTAL;
            }
            //getDialog().getWindow()
            //		.getAttributes().windowAnimations = R.style.DialogAnimation;
            getDialog().getWindow()
                    .getAttributes().gravity = gravity;
        }
    }

    private void toggleTorch(boolean goToSleep) {
        try {
            Intent intent = new Intent(mContext, TorchService.class);
            intent.setAction(TorchService.ACTION_TOGGLE_TORCH);
            intent.putExtra(TorchService.EXTRA_GO_TO_SLEEP, goToSleep);
            mContext.startService(intent);
        } catch (Throwable t) {
            Log.e("TorchService", "Error toggling Torch: " + t.getMessage());
        }
    }

}

