
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 daimajia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.daimajia.easing;


import com.daimajia.easing.attention.BounceAnimator;
import com.daimajia.easing.attention.FlashAnimator;
import com.daimajia.easing.attention.PulseAnimator;
import com.daimajia.easing.attention.RubberBandAnimator;
import com.daimajia.easing.attention.ShakeAnimator;
import com.daimajia.easing.attention.StandUpAnimator;
import com.daimajia.easing.attention.SwingAnimator;
import com.daimajia.easing.attention.TadaAnimator;
import com.daimajia.easing.attention.WaveAnimator;
import com.daimajia.easing.attention.WobbleAnimator;
import com.daimajia.easing.bouncing_entrances.BounceInAnimator;
import com.daimajia.easing.bouncing_entrances.BounceInDownAnimator;
import com.daimajia.easing.bouncing_entrances.BounceInLeftAnimator;
import com.daimajia.easing.bouncing_entrances.BounceInRightAnimator;
import com.daimajia.easing.bouncing_entrances.BounceInUpAnimator;
import com.daimajia.easing.fading_entrances.FadeInAnimator;
import com.daimajia.easing.fading_entrances.FadeInDownAnimator;
import com.daimajia.easing.fading_entrances.FadeInLeftAnimator;
import com.daimajia.easing.fading_entrances.FadeInRightAnimator;
import com.daimajia.easing.fading_entrances.FadeInUpAnimator;
import com.daimajia.easing.fading_exits.FadeOutAnimator;
import com.daimajia.easing.fading_exits.FadeOutDownAnimator;
import com.daimajia.easing.fading_exits.FadeOutLeftAnimator;
import com.daimajia.easing.fading_exits.FadeOutRightAnimator;
import com.daimajia.easing.fading_exits.FadeOutUpAnimator;
import com.daimajia.easing.flippers.FlipInXAnimator;
import com.daimajia.easing.flippers.FlipInYAnimator;
import com.daimajia.easing.flippers.FlipOutXAnimator;
import com.daimajia.easing.flippers.FlipOutYAnimator;
import com.daimajia.easing.rotating_entrances.RotateInAnimator;
import com.daimajia.easing.rotating_entrances.RotateInDownLeftAnimator;
import com.daimajia.easing.rotating_entrances.RotateInDownRightAnimator;
import com.daimajia.easing.rotating_entrances.RotateInUpLeftAnimator;
import com.daimajia.easing.rotating_entrances.RotateInUpRightAnimator;
import com.daimajia.easing.rotating_exits.RotateOutAnimator;
import com.daimajia.easing.rotating_exits.RotateOutDownLeftAnimator;
import com.daimajia.easing.rotating_exits.RotateOutDownRightAnimator;
import com.daimajia.easing.rotating_exits.RotateOutUpLeftAnimator;
import com.daimajia.easing.rotating_exits.RotateOutUpRightAnimator;
import com.daimajia.easing.sliders.SlideInDownAnimator;
import com.daimajia.easing.sliders.SlideInLeftAnimator;
import com.daimajia.easing.sliders.SlideInRightAnimator;
import com.daimajia.easing.sliders.SlideInUpAnimator;
import com.daimajia.easing.sliders.SlideOutDownAnimator;
import com.daimajia.easing.sliders.SlideOutLeftAnimator;
import com.daimajia.easing.sliders.SlideOutRightAnimator;
import com.daimajia.easing.sliders.SlideOutUpAnimator;
import com.daimajia.easing.specials.HingeAnimator;
import com.daimajia.easing.specials.RollInAnimator;
import com.daimajia.easing.specials.RollOutAnimator;
import com.daimajia.easing.specials.in.DropOutAnimator;
import com.daimajia.easing.specials.in.LandingAnimator;
import com.daimajia.easing.specials.out.TakingOffAnimator;
import com.daimajia.easing.zooming_entrances.ZoomInAnimator;
import com.daimajia.easing.zooming_entrances.ZoomInDownAnimator;
import com.daimajia.easing.zooming_entrances.ZoomInLeftAnimator;
import com.daimajia.easing.zooming_entrances.ZoomInRightAnimator;
import com.daimajia.easing.zooming_entrances.ZoomInUpAnimator;
import com.daimajia.easing.zooming_exits.ZoomOutAnimator;
import com.daimajia.easing.zooming_exits.ZoomOutDownAnimator;
import com.daimajia.easing.zooming_exits.ZoomOutLeftAnimator;
import com.daimajia.easing.zooming_exits.ZoomOutRightAnimator;
import com.daimajia.easing.zooming_exits.ZoomOutUpAnimator;

public enum Techniques {

    DropOut(DropOutAnimator.class),
    Landing(LandingAnimator.class),
    TakingOff(TakingOffAnimator.class),

    Flash(FlashAnimator.class),
    Pulse(PulseAnimator.class),
    RubberBand(RubberBandAnimator.class),
    Shake(ShakeAnimator.class),
    Swing(SwingAnimator.class),
    Wobble(WobbleAnimator.class),
    Bounce(BounceAnimator.class),
    Tada(TadaAnimator.class),
    StandUp(StandUpAnimator.class),
    Wave(WaveAnimator.class),

    Hinge(HingeAnimator.class),
    RollIn(RollInAnimator.class),
    RollOut(RollOutAnimator.class),

    BounceIn(BounceInAnimator.class),
    BounceInDown(BounceInDownAnimator.class),
    BounceInLeft(BounceInLeftAnimator.class),
    BounceInRight(BounceInRightAnimator.class),
    BounceInUp(BounceInUpAnimator.class),

    FadeIn(FadeInAnimator.class),
    FadeInUp(FadeInUpAnimator.class),
    FadeInDown(FadeInDownAnimator.class),
    FadeInLeft(FadeInLeftAnimator.class),
    FadeInRight(FadeInRightAnimator.class),

    FadeOut(FadeOutAnimator.class),
    FadeOutDown(FadeOutDownAnimator.class),
    FadeOutLeft(FadeOutLeftAnimator.class),
    FadeOutRight(FadeOutRightAnimator.class),
    FadeOutUp(FadeOutUpAnimator.class),

    FlipInX(FlipInXAnimator.class),
    FlipOutX(FlipOutXAnimator.class),
    FlipInY(FlipInYAnimator.class),
    FlipOutY(FlipOutYAnimator.class),
    RotateIn(RotateInAnimator.class),
    RotateInDownLeft(RotateInDownLeftAnimator.class),
    RotateInDownRight(RotateInDownRightAnimator.class),
    RotateInUpLeft(RotateInUpLeftAnimator.class),
    RotateInUpRight(RotateInUpRightAnimator.class),

    RotateOut(RotateOutAnimator.class),
    RotateOutDownLeft(RotateOutDownLeftAnimator.class),
    RotateOutDownRight(RotateOutDownRightAnimator.class),
    RotateOutUpLeft(RotateOutUpLeftAnimator.class),
    RotateOutUpRight(RotateOutUpRightAnimator.class),

    SlideInLeft(SlideInLeftAnimator.class),
    SlideInRight(SlideInRightAnimator.class),
    SlideInUp(SlideInUpAnimator.class),
    SlideInDown(SlideInDownAnimator.class),

    SlideOutLeft(SlideOutLeftAnimator.class),
    SlideOutRight(SlideOutRightAnimator.class),
    SlideOutUp(SlideOutUpAnimator.class),
    SlideOutDown(SlideOutDownAnimator.class),

    ZoomIn(ZoomInAnimator.class),
    ZoomInDown(ZoomInDownAnimator.class),
    ZoomInLeft(ZoomInLeftAnimator.class),
    ZoomInRight(ZoomInRightAnimator.class),
    ZoomInUp(ZoomInUpAnimator.class),

    ZoomOut(ZoomOutAnimator.class),
    ZoomOutDown(ZoomOutDownAnimator.class),
    ZoomOutLeft(ZoomOutLeftAnimator.class),
    ZoomOutRight(ZoomOutRightAnimator.class),
    ZoomOutUp(ZoomOutUpAnimator.class);


    private Class animatorClazz;

    private Techniques(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
