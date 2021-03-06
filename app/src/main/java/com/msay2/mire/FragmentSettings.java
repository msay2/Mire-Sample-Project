package com.msay2.mire;

import android.os.*;
import android.app.*;

import com.msay2.mire.preferences.Preferences;
import com.msay2.mire.interfaces.MenuAnimation;
import com.msay2.mire.helpers.TransitionHelperSettings;

import no.agens.depth.lib.MaterialMenuDrawable;
import no.agens.depth.lib.DepthLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Switch;

public class FragmentSettings extends Fragment implements MenuAnimation
{
	private View root;
	private boolean introAnimate;
	private ImageView menu;
	private MaterialMenuDrawable menuIcon;
	private LinearLayout clear_cache;
	private RelativeLayout switch_auto_update;
	private TextView text_cache_size;
	private Switch switchUpdate;
	private File fileCache;

	public static final int TRANSFORM_DURATION = 900;

	public static String TAG = FragmentWallpaper.class.getSimpleName();

	public FragmentSettings()
	{ }

	public void setIntroAnimate(boolean introAnimate)
	{
        this.introAnimate = introAnimate;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(R.layout.fragment_settings, null);
		
		DepthLayout second_layout = (DepthLayout)root.findViewById(R.id.second_layout_container);
		second_layout.setCustomShadowElevation(0);
		
		introAnimate();
		setupMenuButton();
		setupCache();
		setupSwitchAutoUpdate();
		setAutoUpdate();

		((MainActivity)getActivity()).setCurretMenuIndex(3);

		return root;
	}

	private void introAnimate()
	{
        if (introAnimate)
		{
			root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
			{
				@Override
				public void onGlobalLayout()
				{
					root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

					TransitionHelperSettings.startIntroAnim(root, showShadowListener);
					showShadow();
				}
			});
		}
    }

	AnimatorListenerAdapter showShadowListener = new AnimatorListenerAdapter()
	{
        @Override
        public void onAnimationEnd(Animator animation) 
		{
            super.onAnimationEnd(animation);
            hideShadow();
        }
    };

	private void hideShadow()
	{
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.GONE);
    }

    private void showShadow() 
	{
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.VISIBLE);

		ObjectAnimator.ofFloat(actionbarShadow, View.ALPHA, 0, 0.8f).setDuration(400).start();
    }

	private void setupMenuButton() 
	{
        menu = (ImageView)root.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!((MainActivity)getActivity()).isMenuVisible)
				{
					((MainActivity)getActivity()).showMenu();
				}
				else
				{
					((MainActivity)getActivity()).onBackPressed();
				}
			}
		});
        menuIcon = new MaterialMenuDrawable(getActivity(), Color.WHITE, MaterialMenuDrawable.Stroke.THIN, TRANSFORM_DURATION);
        menu.setImageDrawable(menuIcon);
    }
	
	private void setupCache()
	{
		clear_cache = (LinearLayout)root.findViewById(R.id.id_clear_cache);
		text_cache_size = (TextView)root.findViewById(R.id.id_text_cache_size);
		
		initCache();
		clear_cache.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				clearCache(fileCache);
				initCache();
			}
		});
	}
	
	private void setupSwitchAutoUpdate()
	{
		switch_auto_update = (RelativeLayout)root.findViewById(R.id.id_auto_update);
		switchUpdate = (Switch)root.findViewById(R.id.id_switch_auto_update);
		
		switch_auto_update.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (Preferences.getPreferences(getActivity()).getNoAutoUpdate())
				{
					Preferences.getPreferences(getActivity()).enableAutoUpdate();
					switchUpdate.setChecked(true);
				}
				else if (Preferences.getPreferences(getActivity()).getAutoUpdate())
				{
					Preferences.getPreferences(getActivity()).enableNoAutoUpdate();
					switchUpdate.setChecked(false);
				}
				else
				{
					Preferences.getPreferences(getActivity()).enableAutoUpdate();
					switchUpdate.setChecked(true);
				}
			}
		});
		switchUpdate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (switchUpdate.isChecked())
				{
					Preferences.getPreferences(getActivity()).enableAutoUpdate();
				}
				else
				{
					Preferences.getPreferences(getActivity()).enableNoAutoUpdate();
				}
			}
		});
	}
	
	private void initCache()
	{
		fileCache = new File(getActivity().getCacheDir().toString());

        double cache = (double)cacheSize(fileCache)/1024/1024;
        NumberFormat formatter = new DecimalFormat("#0.00");
		
		String size = getActivity().getResources().getString(R.string.fragment_settings_cache_size);
		String scacheSize = size + (formatter.format(cache)) + " MB";
		
		text_cache_size.setText(scacheSize);
	}
	
	private void clearCache(File fileOrDirectory) 
	{
        if (fileOrDirectory.isDirectory())
		{
			for (File child : fileOrDirectory.listFiles())
			{
				clearCache(child);
			}
		}
        fileOrDirectory.delete();
    }
	
	private long cacheSize(File dir)
	{
        if (dir.exists())
		{
            long result = 0;
            File[] fileList = dir.listFiles();
            for (File aFileList : fileList)
			{
                if (aFileList.isDirectory())
				{
                    result += cacheSize(aFileList);
                }
				else 
				{
                    result += aFileList.length();
                }
            }
            return result;
        }
        return 0;
    }
	
	private void setAutoUpdate()
	{
		if (Preferences.getPreferences(getActivity()).getNoAutoUpdate())
		{
			switchUpdate.setChecked(false);
		}
		else if (Preferences.getPreferences(getActivity()).getAutoUpdate())
		{
			switchUpdate.setChecked(true);
		}
		else
		{
			switchUpdate.setChecked(false);
		}
	}

	@Override
	public void animateTOMenu()
	{
		TransitionHelperSettings.animateToMenuState(root, new AnimatorListenerAdapter() 
		{
			@Override
			public void onAnimationEnd(Animator animation) 
			{
				super.onAnimationEnd(animation);
			}
		});
		menuIcon.animateIconState(MaterialMenuDrawable.IconState.ARROW);
		showShadow();
	}

	@Override
	public void revertFromMenu()
	{
		TransitionHelperSettings.startRevertFromMenu(root, showShadowListener);
		menuIcon.animateIconState(MaterialMenuDrawable.IconState.BURGER);
	}

	@Override
	public void exitFromMenu()
	{
		TransitionHelperSettings.animateMenuOut(root);
	}
}
