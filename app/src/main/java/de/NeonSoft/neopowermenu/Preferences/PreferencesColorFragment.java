package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.content.ClipboardManager;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.larswerkman.holocolorpicker.*;

import de.NeonSoft.neopowermenu.*;

import java.io.*;

import android.support.v4.app.Fragment;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.*;

import java.util.*;

public class PreferencesColorFragment extends Fragment {

    Context mContext;

    public static android.content.ClipboardManager cbM;

    ListView ListView_ColorsList;
    ColorsListAdapter adapter;

    public static String[] lightPreset = {
            "Presets", "Load", "Save",
            "Main", "#8800bcd4", "#fff5f5f5", "#000000",
            "Shutdown", "#ff0097a7", "#ffd32f2f", "#ffd32f2f", "#ffffff",
            "Reboot", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff",
            "SoftReboot", "#ff0097a7", "#ffe91e63", "#ffe91e63", "#ffffff",
            "Screenshot", "#ff3f51b5", "#ffffff",
            "Screenrecord", "#ff3f51b5", "#ffffff",
            "Flashlight", "#ff3f51b5", "#ffffff",
            "ExpandedDesktop", "#ff3f51b5", "#ffffff",
            "AirplaneMode", "#ff3f51b5", "#ffffff",
            "RestartUI", "#ff3f51b5", "#ffffff",
            "SoundMode", "#ff3f51b5", "#ffffff",
            "Recovery", "#ff0097a7", "#ff8bc34a", "#ff8bc34a", "#ffffff",
            "Bootloader", "#ff0097a7", "#ff277b71", "#ff277b71", "#ffffff",
            "SafeMode", "#ff0097a7", "#ff009698", "#ff009698", "#ffffff",
            "SoundVibrate", "#ff3f51b5", "#ffffff",
            "SoundNormal", "#ff3f51b5", "#ffffff",
            "SoundSilent", "#ff3f51b5", "#ffffff",
            "KillApp", "#ff3f51b5", "#ffffff",
            "AppShortcut", "#ff3f51b5", "#ffffff",
            "ToggleRotate", "#ff3f51b5", "#ffffff",
            "MediaPrevious", "#ff3f51b5", "#ffffff",
            "MediaPlayPause", "#ff3f51b5", "#ffffff",
            "MediaNext", "#ff3f51b5", "#ffffff",
            "ToggleWifi", "#ff3f51b5", "#ffffff",
            "ToggleBluetooth", "#ff3f51b5", "#ffffff",
            "ToggleData", "#ff3f51b5", "#ffffff",
            "RebootFlashMode", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff",
            "LockPhone", "#ff3f51b5", "#ffffff",
            "SilentMode", "#ff3f51b5", "#ffffff",
            "Shortcut", "#ff3f51b5", "#ffffff"};

    public static String[] darkPreset = {
            "Presets", "Load", "Save",
            "Main", "#88121212", "#ff212121", "#ffffff",
            "Shutdown", "#ff0097a7", "#ffd32f2f", "#ffd32f2f", "#ffffff",
            "Reboot", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff",
            "SoftReboot", "#ff0097a7", "#ffe91e63", "#ffe91e63", "#ffffff",
            "Screenshot", "#ff3f51b5", "#ffffff",
            "Screenrecord", "#ff3f51b5", "#ffffff",
            "Flashlight", "#ff3f51b5", "#ffffff",
            "ExpandedDesktop", "#ff3f51b5", "#ffffff",
            "AirplaneMode", "#ff3f51b5", "#ffffff",
            "RestartUI", "#ff3f51b5", "#ffffff",
            "SoundMode", "#ff3f51b5", "#ffffff",
            "Recovery", "#ff0097a7", "#ff8bc34a", "#ff8bc34a", "#ffffff",
            "Bootloader", "#ff0097a7", "#ff277b71", "#ff277b71", "#ffffff",
            "SafeMode", "#ff0097a7", "#ff009698", "#ff009698", "#ffffff",
            "SoundVibrate", "#ff3f51b5", "#ffffff",
            "SoundNormal", "#ff3f51b5", "#ffffff",
            "SoundSilent", "#ff3f51b5", "#ffffff",
            "KillApp", "#ff3f51b5", "#ffffff",
            "AppShortcut", "#ff3f51b5", "#ffffff",
            "ToggleRotate", "#ff3f51b5", "#ffffff",
            "MediaPrevious", "#ff3f51b5", "#ffffff",
            "MediaPlayPause", "#ff3f51b5", "#ffffff",
            "MediaNext", "#ff3f51b5", "#ffffff",
            "ToggleWifi", "#ff3f51b5", "#ffffff",
            "ToggleBluetooth", "#ff3f51b5", "#ffffff",
            "ToggleData", "#ff3f51b5", "#ffffff",
            "RebootFlashMode", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff",
            "LockPhone", "#ff3f51b5", "#ffffff",
            "SilentMode", "#ff3f51b5", "#ffffff",
            "Shortcut", "#ff3f51b5", "#ffffff"};

    public static String[] blackPreset = {
            "Presets", "Load", "Save",
            "Main", "#88000000", "#ff000000", "#ffffff",
            "Shutdown", "#ff000000", "#ff000000", "#ff000000", "#ffffff",
            "Reboot", "#ff000000", "#ff000000", "#ff000000", "#ffffff",
            "SoftReboot", "#ff000000", "#ff000000", "#ff000000", "#ffffff",
            "Screenshot", "#ff000000", "#ffffff",
            "Screenrecord", "#ff000000", "#ffffff",
            "Flashlight", "#ff000000", "#ffffff",
            "ExpandedDesktop", "#ff000000", "#ffffff",
            "AirplaneMode", "#ff000000", "#ffffff",
            "RestartUI", "#ff000000", "#ffffff",
            "SoundMode", "#ff000000", "#ffffff",
            "Recovery", "#ff000000", "#ff000000", "#ff000000", "#ffffff",
            "Bootloader", "#ff000000", "#ff000000", "#ff000000", "#ffffff",
            "SafeMode", "#ff000000", "#ff000000", "#ff000000", "#ffffff",
            "SoundVibrate", "#ff000000", "#ffffff",
            "SoundNormal", "#ff000000", "#ffffff",
            "SoundSilent", "#ff000000", "#ffffff",
            "KillApp", "#ff000000", "#ffffff",
            "AppShortcut", "#ff000000", "#ffffff",
            "ToggleRotate", "#ff000000", "#ffffff",
            "MediaPrevious", "#ff000000", "#ffffff",
            "MediaPlayPause", "#ff000000", "#ffffff",
            "MediaNext", "#ff000000", "#ffffff",
            "ToggleWifi", "#ff000000", "#ffffff",
            "ToggleBluetooth", "#ff000000", "#ffffff",
            "ToggleData", "#ff000000", "#ffffff",
            "RebootFlashMode", "#ff000000", "#ff000000", "#ff000000", "#ffffff",
            "LockPhone", "#ff000000", "#ffffff",
            "SilentMode", "#ff000000", "#ffffff",
            "Shortcut", "#ff000000", "#ffffff"};

    public static Object[][] ColorNames = {
            {ColorsListAdapter.TYPE_HEADER, "Presets"}, {ColorsListAdapter.TYPE_LOAD, "Load"}, {ColorsListAdapter.TYPE_SAVE, "Save"},
            {ColorsListAdapter.TYPE_HEADER, "Main"}, {ColorsListAdapter.TYPE_ITEM, "Dialog_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "Dialog_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "Dialog_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Shutdown"}, {ColorsListAdapter.TYPE_ITEM, "DialogShutdown_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogShutdown_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogShutdown_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogShutdown_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Reboot"}, {ColorsListAdapter.TYPE_ITEM, "DialogReboot_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogReboot_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogReboot_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogReboot_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoftReboot"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoftReboot_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoftReboot_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoftReboot_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoftReboot_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Screenshot"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenshot_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenshot_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Screenrecord"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenrecord_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenrecord_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Flashlight"}, {ColorsListAdapter.TYPE_ITEM, "DialogFlashlight_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogFlashlight_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "ExpandedDesktop"}, {ColorsListAdapter.TYPE_ITEM, "DialogExpandedDesktop_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogExpandedDesktop_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "AirplaneMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogAirplaneMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogAirplaneMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "RestartUI"}, {ColorsListAdapter.TYPE_ITEM, "DialogRestartUI_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRestartUI_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Recovery"}, {ColorsListAdapter.TYPE_ITEM, "DialogRecovery_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRecovery_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRecovery_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRecovery_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Bootloader"}, {ColorsListAdapter.TYPE_ITEM, "DialogBootloader_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogBootloader_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogBootloader_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogBootloader_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SafeMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogSafeMode_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSafeMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSafeMode_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSafeMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundVibrate"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundVibrate_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundVibrate_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundNormal"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundNormal_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundNormal_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundSilent"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundSilent_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundSilent_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "KillApp"}, {ColorsListAdapter.TYPE_ITEM, "DialogKillApp_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogKillApp_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "AppShortcut"}, {ColorsListAdapter.TYPE_ITEM, "DialogAppShortcut_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogAppShortcut_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "ToggleRotate"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleRotate_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleRotate_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "MediaPrevious"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPrevious_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPrevious_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "MediaPlayPause"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPlayPause_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPlayPause_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "MediaNext"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaNext_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaNext_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "ToggleWifi"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleWifi_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleWifi_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "ToggleBluetooth"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleBluetooth_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleBluetooth_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "ToggleData"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleData_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleData_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "RebootFlashMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogRebootFlashMode_Revealcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRebootFlashMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRebootFlashMode_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRebootFlashMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "LockPhone"}, {ColorsListAdapter.TYPE_ITEM, "DialogLockPhone_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogLockPhone_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SilentMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogSilentMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSilentMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Shortcut"}, {ColorsListAdapter.TYPE_ITEM, "DialogShortcut_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogShortcut_Textcolor"}};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "CustomColors";
        }
        View InflatedView = inflater.inflate(R.layout.activity_colorpreferences, container, false);

        mContext = getActivity();
        cbM = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        MainActivity.actionbar.setTitle(getString(R.string.preferences_ThemeTitle));
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_ThemeDesc));

        ListView_ColorsList = (ListView) InflatedView.findViewById(R.id.activitycolorpreferencesListView_Colors);

        adapter = new ColorsListAdapter(getActivity(), ColorNames, lightPreset);
        ListView_ColorsList.setFastScrollEnabled(true);
        ListView_ColorsList.setAdapter(adapter);


        return InflatedView;
    }

}
