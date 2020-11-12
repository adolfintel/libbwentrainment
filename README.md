# LibBWEntrainment

LibBWEntrainment is a Free and Open Source Brainwave Entrainment Library for Java.
The library is very flexible, allowing different types of renderers as well as multiple output devices (speakers, file, socket, ...), and it can easily be ported to other languages because it doesn't use any proprietary Java feature.

[SINE Isochronic Entrainer](https://sine.fdossena.com) is based on this library.

## Components
* __Core library__: LibBWEntrainment. This is always required
* __Renderers__: only the Isochronic renderer is available at the moment
    * Isochronic: renders presets to a sound backend as Isochronic tones
* __Sound Backends__: pick the ones you need
    * PC: outputs to a regular PC sound card
    * Android: outputs to an Android AudioTrack
    * MP3 (Requires Java 7): outputs to an MP3 file. Uses [JavaLAME](https://github.com/nwaldispuehl/java-lame)
    * FLAC: outputs to a FLAC file. Uses [Java FLAC Encoder by Preston Lacey](http://javaflacencoder.sourceforge.net/javadoc/javaFlacEncoder/FLACEncoder.html)
    * Wav: outputs to an uncompressed .wav file
    * Stream: outputs to an OutputStream (useful for writing to a socket or a byte array)
    * Null: a fake backend that does nothing, only useful for testing

## Usage
To compile the projects, import them in Netbeans.
Alternatively, you can get precompiled binaries from [my site](https://downloads.fdossena.com/geth.php?r=libbwentrainment-bin)

## Compatibility
__PC__: Multiplatform, Java SE 6 or newer (MP3 support requires Java SE 7).

__Android__: Android 2.3 (SDK 9) or newer. All architectures are supported.

A partial Javascript port with playback capabilities only is available as part of the [SINE HTML5 port](https://github.com/adolfintel/sine-html5)

## License
Copyright (C) 2014-2020 Federico Dossena

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/lgpl>.
