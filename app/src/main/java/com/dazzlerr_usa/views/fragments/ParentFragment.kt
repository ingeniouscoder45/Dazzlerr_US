package com.dazzlerr_usa.views.fragments


import android.os.Bundle
import android.view.animation.Animation

import com.labo.kaji.fragmentanimations.CubeAnimation

open class ParentFragment : androidx.fragment.app.Fragment() {

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        return if (enter)
        {
            CubeAnimation.create(CubeAnimation.LEFT, enter, 700)
        } else {
            CubeAnimation.create(CubeAnimation.LEFT, enter, 700)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}
