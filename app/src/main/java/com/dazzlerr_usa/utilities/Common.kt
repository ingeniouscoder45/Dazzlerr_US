package com.dazzlerr_usa.utilities

import android.app.Activity
import android.content.Context
import com.dazzlerr_usa.R
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu


class Common {

    fun menu(c: Context) {
        menu.showMenu()
    }

    companion object {

        internal lateinit var menu: SlidingMenu
        fun setWidth(c: Context): Int {
            val display = (c as Activity).windowManager.defaultDisplay

            val w = display.width

            return w / 2 + 200

        }

        fun setSlidingMenu(c: Context): SlidingMenu {

            menu = SlidingMenu(c)

            try {
                menu.setFadeDegree(0.8f)
                menu.setFadeEnabled(true)
                menu.behindOffset = 10

                menu.setBehindWidth(Common.setWidth(c))
                menu.attachToActivity(c as Activity, SlidingMenu.SLIDING_CONTENT)
                menu.setMenu(R.layout.slide_menu_layout)
                menu.mode = SlidingMenu.LEFT
                menu.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN

            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }


            return menu
        }
    }

}
