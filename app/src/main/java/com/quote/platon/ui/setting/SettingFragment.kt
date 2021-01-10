package com.quote.platon.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.quote.platon.R


class SettingFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingViewModel =
            activity?.let { ViewModelProviders.of(it).get(SettingViewModel::class.java) }!!

        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        val musicSwitch: Switch = root.findViewById(R.id.musicSwitch)
        val swipeSoundEffect: Switch = root.findViewById(R.id.swipeSoundEffectSwitch)
        val landscapeOrientationSwitch: Switch = root.findViewById(R.id.landscapeOrientationSwitch)

        val preference =
            context?.getSharedPreferences(
                resources.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )

        val editor = preference?.edit()

        musicSwitch.isChecked = preference?.getBoolean("music", true)!!
        swipeSoundEffect.isChecked = preference?.getBoolean("swipeSoundEffect", true)!!
        landscapeOrientationSwitch.isChecked = preference?.getBoolean("landscapeOrientation", true)!!


        landscapeOrientationSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (editor != null) {
                editor.putBoolean("landscapeOrientation", isChecked)
                editor.commit()
            }
            settingViewModel.landscapeOrientation.postValue(isChecked)
            // do something, the isChecked will be
            // true if the switch is in the On position
        })


        musicSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (editor != null) {
                editor.putBoolean("music", isChecked)
                editor.commit()
            }
            settingViewModel.musicState.postValue(isChecked)
            // do something, the isChecked will be
            // true if the switch is in the On position
        })

        swipeSoundEffect.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (editor != null) {
                editor.putBoolean("swipeSoundEffect", isChecked)
                editor.commit()
            }
            settingViewModel.swipeEffect.postValue(isChecked)

            // do something, the isChecked will be
            // true if the switch is in the On position
        })



        return root
    }
}
