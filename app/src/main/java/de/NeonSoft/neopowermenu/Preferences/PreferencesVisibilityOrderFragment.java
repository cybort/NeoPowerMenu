package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.os.*;
import android.view.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.DSLV.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;
import android.support.v4.app.Fragment;
import de.NeonSoft.neopowermenu.xposed.*;

public class PreferencesVisibilityOrderFragment extends Fragment
{
		

    private visibilityOrder_ListAdapter adapter;
		
    private String[] arrayTitles;
    private ArrayList<String> listTitles;
		private String[] arrayEnabledStates;
		private ArrayList<String> listEnabledStates;
		
    private DragSortListView.DropListener onDrop =
		new DragSortListView.DropListener() {
				@Override
				public void drop(int from, int to) {
						if (from != to) {
								String[] item=adapter.getItemAt(from);
								
								adapter.removeAt(from);
								adapter.insertAt(item, to);
								adapter.OutputSorting();
								adapter.notifyDataSetChanged();
						}
				}
		};

    private DragSortListView.RemoveListener onRemove = 
		new DragSortListView.RemoveListener() {
				@Override
				public void remove(int which) {
						adapter.remove(adapter.getItem(which));
				}
		};

    private DragSortListView.DragScrollProfile ssProfile =
		new DragSortListView.DragScrollProfile() {
				@Override
				public float getSpeed(float w, long t) {
						if (w > 0.8f) {
								// Traverse all views in a millisecond
								return ((float) adapter.getCount()) / 0.001f;
						} else {
								return 10.0f * w;
						}
				}
		};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				
				MainActivity.visibleFragment = "VisibilityOrder";
				View InflatedView = inflater.inflate(R.layout.activity_visibilityorder,container,false);
				
        DragSortListView lv = (DragSortListView) InflatedView.findViewById(R.id.activityvisibilityorderDSLV_List); 
				
        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
        lv.setDragEnabled(true);
        lv.setDragScrollProfile(ssProfile);
				lv.setFastScrollEnabled(true);
				
				if (MainActivity.preferences.getInt("ShutdownPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("ShutdownEnabled",true).commit();
						MainActivity.preferences.edit().putInt("ShutdownPosition",0).commit();
				}
				if (MainActivity.preferences.getInt("RebootPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("RebootEnabled",true).commit();
						MainActivity.preferences.edit().putInt("RebootPosition",1).commit();
				}
				if (MainActivity.preferences.getInt("SoftRebootPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("SoftRebootEnabled",true).commit();
						MainActivity.preferences.edit().putInt("SoftRebootPosition",2).commit();
				}
				if (MainActivity.preferences.getInt("ScreenshotPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("ScreenshotEnabled",false).commit();
						MainActivity.preferences.edit().putInt("ScreenshotPosition",3).commit();
				}
				if (MainActivity.preferences.getInt("ScreenrecordPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("ScreenrecordEnabled",false).commit();
						MainActivity.preferences.edit().putInt("ScreenrecordPosition",4).commit();
				}
				if (MainActivity.preferences.getInt("FlashlightPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("FlashlightEnabled",false).commit();
						MainActivity.preferences.edit().putInt("FlashlightPosition",5).commit();
				}
				if (MainActivity.preferences.getInt("ExpandedDesktopPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("ExpandedDesktopEnabled",false).commit();
						MainActivity.preferences.edit().putInt("ExpandedDesktopPosition",6).commit();
				}
				if (MainActivity.preferences.getInt("AirplaneModePosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("AirplaneModeEnabled",false).commit();
						MainActivity.preferences.edit().putInt("AirplaneModePosition",7).commit();
				}
				if (MainActivity.preferences.getInt("RestartUIPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("RestartUIEnabled",false).commit();
						MainActivity.preferences.edit().putInt("RestartUIPosition",8).commit();
				}
				
        arrayTitles = new String[9];
				arrayTitles[MainActivity.preferences.getInt("ShutdownPosition",0)] = "Shutdown";
				arrayTitles[MainActivity.preferences.getInt("RebootPosition",1)] = "Reboot";
				arrayTitles[MainActivity.preferences.getInt("SoftRebootPosition",2)] = "SoftReboot";
				arrayTitles[MainActivity.preferences.getInt("ScreenshotPosition",3)] = "Screenshot";
				arrayTitles[MainActivity.preferences.getInt("ScreenrecordPosition",4)] = "Screenrecord";
				arrayTitles[MainActivity.preferences.getInt("FlashlightPosition",5)] = "Flashlight";
				arrayTitles[MainActivity.preferences.getInt("ExpandedDesktopPosition",6)] = "ExpandedDesktop";
				arrayTitles[MainActivity.preferences.getInt("AirplaneModePosition",7)] = "AirplaneMode";
				arrayTitles[MainActivity.preferences.getInt("RestartUIPosition",8)] = "RestartUI";
        listTitles = new ArrayList<String>(Arrays.asList(arrayTitles));
					
				arrayEnabledStates = new String[9];
				arrayEnabledStates[MainActivity.preferences.getInt("ShutdownPosition",0)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("RebootPosition",1)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("SoftRebootPosition",2)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("ScreenshotPosition",3)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("ScreenrecordPosition",4)] = XposedUtils.isExynosDevice() ? "false" : "true";
				arrayEnabledStates[MainActivity.preferences.getInt("FlashlightPosition",5)] = XposedUtils.hasFlash(getActivity()) ? "true" : "false";
				arrayEnabledStates[MainActivity.preferences.getInt("ExpandedDesktopPosition",6)] = (XposedUtils.isAppInstalled(getActivity(),"com.ceco.kitkat.gravitybox") || XposedUtils.isAppInstalled(getActivity(),"com.ceco.lollipop.gravitybox") || XposedUtils.isAppInstalled(getActivity(),"com.ceco.marshmallow.gravitybox")) ? "true" : "false";
				arrayEnabledStates[MainActivity.preferences.getInt("AirplaneModePosition",7)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("RestartUIPosition",8)] = "true";
				listEnabledStates = new ArrayList<String>(Arrays.asList(arrayEnabledStates));
				
        adapter = new visibilityOrder_ListAdapter(getActivity(),listTitles,listEnabledStates);
				
        lv.setAdapter(adapter);
						
				return InflatedView;
		}
		
}
