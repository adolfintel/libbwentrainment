/*
 * Copyright (C) 2014 Federico Dossena.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
import com.dosse.bwentrain.core.EntrainmentTrack;
import com.dosse.bwentrain.core.Envelope;
import com.dosse.bwentrain.core.Preset;
import com.dosse.bwentrain.renderers.isochronic.IsochronicRenderer;
import com.dosse.bwentrain.sound.backends.pc.PCSoundBackend;

/**
 * A basic tutorial on how to use LibBWEntrainment
 * @author dosse
 */
public class Example {
    public static void main(String args[]){
        //this example will show you how to use LibBWEntrainment
        //first, we'll create an empty Preset
        Preset p=new Preset(300, 290, "Test preset", "dosse", "This simple preset shows off all the features of LibBWEntrainment"); //5 minutes long, loops after 4:50
        //right now the Preset is pretty much empty: it contains 1 noise Envelope with only 1 point with time=0 and value=0, and 1 EntrainmentTrack, which contains 3 envelopes identical to the empty noise one
        //so first we'll change the value of the first point of the noise Envelope
        Envelope n=p.getNoiseEnvelope();
        n.setVal(0, 0.45f); //constant 45% noise
        //let's say we want to make a 5 second fade-in: we need to add another point
        n.addPoint(5, 0.45f); //this second point has time=5 and value=0.45, just like the first one
        n.setVal(0, 0); //we set the first point to val=0, so now during the first 5 seconds, there will be a fade-in from 0 to 0.45
        //let's add another point
        n.addPoint(200, 0.3f); //at time=200, the noise will have gradually faded to 30% amplitude
        //that's enough for the noise, let's edit the EntrainmentTrack
        EntrainmentTrack e=p.getEntrainmentTrack(0); //get the first EntrainmentTrack
        Envelope entF=e.getEntrainmentFrequencyEnvelope(), //Entrainment Frequency Envelope, controls the frequency of the pulses
                vol=e.getVolumeEnvelope(), //Volume Envelope
                baseF=e.getBaseFrequencyEnvelope(); //Base Frequency Envelope, controls the frequency of the sine wave being modulated
        //first, we'll set the Entrainment Frequency to start at 10Hz (awake) and decrease to 6Hz (relaxed) in 200 seconds
        entF.setVal(0, 10);
        entF.addPoint(200, 6);
        //and finally go down to 4Hz (sleep) at 290s (after which the preset will be looped)
        entF.addPoint(290, 4);
        //if necessary, you can move the points in time using the setT method
        //now for the volume, we'll do a simple fade-in
        vol.addPoint(5, 1); //there's already a point at t=0 val=0, so we'll just add another one at t=5, val=1
        //and finally, for the Base Frequency we'll have a constant 440Hz
        baseF.setVal(0, 440);
        //keep in mind that there are some restrictions to what you can do to points, see the javadoc for details
        //now let's say we want to make some sort of chord: we can duplicate the first EntrainmentTrack, change its Base Frequency and track volume, and we're done
        p.cloneTrack(0); //now we have a second Entrainment Track (1)
        EntrainmentTrack e2=p.getEntrainmentTrack(1);
        //let's change its Base Frequency to 880Hz
        e2.getBaseFrequencyEnvelope().setVal(0, 880);
        //and the Track Volume to 0.6
        e2.setTrackVolume(0.6f);
        //you can add as many EntrainmentTracks as you want, each one potentially doing different things
        //make sure the frequencies you use are not dissonant!
        try {
            //we will now play our Preset
            IsochronicRenderer r=new IsochronicRenderer(p, new PCSoundBackend(44100, 1), -1); //play the Preset p on PCSoundBackend at 44100Hz, Mono, and loop infinitely (don't do this on a file, of course)
            r.play();
            while(true){
                System.out.println(r.getPosition()+" / "+r.getLength());
                Thread.sleep(100);
            }
            //you can also pause and stop the playback using the pause and stopPlaying methods, as well as controlling the position with the setPosition method.
            //if the preset doesn't loop (or if it looped enough times) it will automatically pause and set its position to 0. you can use the isPlaying method to know if it's playing or not.
            //do not try calling play after stopPlaying, as the sound backend will be already closed and it may throw exceptions
        } catch (Exception ex) { 
            //something went wrong
            System.err.println(ex);
        }
        //and that's pretty much you need to know to use LibBWEntrainment. You can find a lot of frequencies to use online. HAVE FUN!
    }
}
