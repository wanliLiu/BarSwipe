
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

package library;


import library.attention.BounceAnimator;
import library.attention.FlashAnimator;
import library.attention.PulseAnimator;
import library.attention.RubberBandAnimator;
import library.attention.ShakeAnimator;
import library.attention.StandUpAnimator;
import library.attention.SwingAnimator;
import library.attention.TadaAnimator;
import library.attention.WaveAnimator;
import library.attention.WobbleAnimator;
import library.bouncing_entrances.BounceInAnimator;
import library.bouncing_entrances.BounceInDownAnimator;
import library.bouncing_entrances.BounceInLeftAnimator;
import library.bouncing_entrances.BounceInRightAnimator;
import library.bouncing_entrances.BounceInUpAnimator;
import library.fading_entrances.FadeInAnimator;
import library.fading_entrances.FadeInDownAnimator;
import library.fading_entrances.FadeInLeftAnimator;
import library.fading_entrances.FadeInRightAnimator;
import library.fading_entrances.FadeInUpAnimator;
import library.fading_exits.FadeOutAnimator;
import library.fading_exits.FadeOutDownAnimator;
import library.fading_exits.FadeOutLeftAnimator;
import library.fading_exits.FadeOutRightAnimator;
import library.fading_exits.FadeOutUpAnimator;
import library.flippers.FlipInXAnimator;
import library.flippers.FlipInYAnimator;
import library.flippers.FlipOutXAnimator;
import library.flippers.FlipOutYAnimator;
import library.rotating_entrances.RotateInAnimator;
import library.rotating_entrances.RotateInDownLeftAnimator;
import library.rotating_entrances.RotateInDownRightAnimator;
import library.rotating_entrances.RotateInUpLeftAnimator;
import library.rotating_entrances.RotateInUpRightAnimator;
import library.rotating_exits.RotateOutAnimator;
import library.rotating_exits.RotateOutDownLeftAnimator;
import library.rotating_exits.RotateOutDownRightAnimator;
import library.rotating_exits.RotateOutUpLeftAnimator;
import library.rotating_exits.RotateOutUpRightAnimator;
import library.sliders.SlideInDownAnimator;
import library.sliders.SlideInLeftAnimator;
import library.sliders.SlideInRightAnimator;
import library.sliders.SlideInUpAnimator;
import library.sliders.SlideOutDownAnimator;
import library.sliders.SlideOutLeftAnimator;
import library.sliders.SlideOutRightAnimator;
import library.sliders.SlideOutUpAnimator;
import library.specials.HingeAnimator;
import library.specials.RollInAnimator;
import library.specials.RollOutAnimator;
import library.specials.in.DropOutAnimator;
import library.specials.in.LandingAnimator;
import library.specials.out.TakingOffAnimator;
import library.zooming_entrances.ZoomInAnimator;
import library.zooming_entrances.ZoomInDownAnimator;
import library.zooming_entrances.ZoomInLeftAnimator;
import library.zooming_entrances.ZoomInRightAnimator;
import library.zooming_entrances.ZoomInUpAnimator;
import library.zooming_exits.ZoomOutAnimator;
import library.zooming_exits.ZoomOutDownAnimator;
import library.zooming_exits.ZoomOutLeftAnimator;
import library.zooming_exits.ZoomOutRightAnimator;
import library.zooming_exits.ZoomOutUpAnimator;

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
