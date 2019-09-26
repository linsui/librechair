/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.anim

import android.view.View
import com.android.launcher3.R
import kotlin.reflect.KClass

interface AnimationDelegate {
    fun animate(content: View, background: View, width: Float, progress: Float)
    val shouldScroll
        get() = true

    companion object {
        fun inflate(clazz: KClass<out AnimationDelegate>): AnimationDelegate =
                clazz.constructors.first { it.parameters.isEmpty() }.call()

        val allDelegates =
                listOf(DefaultFeedTransitionDelegate::class,
                        DefaultDrawerFeedTransitionDelegate::class,
                        SlidingFeedTransitionDelegate::class,
                        GlidingFeedTransitionDelegate::class,
                        GlidingDrawerFeedTransitionDelegate::class,
                        FadingFeedTransitionDelegate::class,
                        ScalingFeedTransitionDelegate::class,
                        SlideTopFeedTransitionDelegate::class,
                        SlideUpFeedTransitionDelegate::class,
                        DrawerFeedTransitionDelegate::class)
        val delegateNames =
                mapOf(DefaultFeedTransitionDelegate::class to R.string.title_animation_delegate_default,
                        DrawerFeedTransitionDelegate::class to R.string.title_animation_delegate_default_drawer,
                        SlidingFeedTransitionDelegate::class to R.string.title_animation_delegate_slide,
                        GlidingFeedTransitionDelegate::class to R.string.title_animation_delegate_glide,
                        GlidingDrawerFeedTransitionDelegate::class to R.string.title_animation_delegate_gliding_drawer,
                        ScalingFeedTransitionDelegate::class to R.string.title_animation_delegate_scale,
                        FadingFeedTransitionDelegate::class to R.string.title_animation_delegate_fading,
                        SlideTopFeedTransitionDelegate::class to R.string.title_animation_delegate_slide_top,
                        SlideUpFeedTransitionDelegate::class to R.string.title_animation_delegate_slide_up,
                        DrawerFeedTransitionDelegate::class to R.string.title_animation_delegate_drawer)
    }
}

@Suppress("UNCHECKED_CAST")
fun AnimationDelegate.Companion.inflate(clazz: String) = inflate(
        Class.forName(clazz).kotlin as KClass<out AnimationDelegate>)