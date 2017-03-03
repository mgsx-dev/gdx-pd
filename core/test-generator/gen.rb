

# http://www.rubydoc.info/gems/midilib/2.0.5/MIDI/Track

require 'midilib/io/seqwriter'
require 'midilib'

include MIDI

# some coomon default values
chan = 0
val = 64
vel = 127
delta = 0

# http://www.music.mcgill.ca/~ich/classes/mumt306/StandardMIDIfileformat.html

seq = Sequence.new
seq.tracks << track = Track.new(seq)

data = [12,13,14,15,16] # NOT SURE
track.events << SystemExclusive.new(data, delta)

File.open('test-sysex.mid', 'wb') { | file | seq.write(file) }



seq = Sequence.new
seq.tracks << track = Track.new(seq)

# channel : 7 types (OK)
track.events << ChannelPressure.new(chan, 13, delta)
track.events << Controller.new(chan, val, vel, delta)
track.events << NoteOff.new(chan, val, vel, delta)
track.events << NoteOn.new(chan, val, vel, delta)
track.events << PolyPressure.new(chan, val, vel, delta)
track.events << PitchBend.new(chan, 569, delta)
track.events << ProgramChange.new(chan, 32, delta)

File.open('test-basic.mid', 'wb') { | file | seq.write(file) }


seq = Sequence.new

track = Track.new(seq)

seq.tracks << track

# META_INSTRUMENT and META_TRACK_END are automatically added to tracks, no need to include then.

# meta : 14 meta events (+ sequencer specific) : OK

# track.events << MetaEvent.new(META_INSTRUMENT, [], delta)

track.events << KeySig.new(-5, true, delta) # TODO ?
track.events << Marker.new("my marker!", delta)
track.events << Tempo.new(Tempo.bpm_to_mpq(147), delta) # TODO doesnt support floats ...
track.events << TimeSig.new(6,8,96,85,delta) # TODO ?

# TODO data ?
track.events << MetaEvent.new(META_SEQ_NUM, [42, 12], delta)
track.events << MetaEvent.new(META_TEXT, "hello world", delta)
track.events << MetaEvent.new(META_COPYRIGHT, "it's mine!", delta)
track.events << MetaEvent.new(META_SEQ_NAME, "my song!", delta)
track.events << MetaEvent.new(META_LYRIC, "blah blah", delta)
track.events << MetaEvent.new(META_CUE, "now!", delta)
track.events << MetaEvent.new(META_MIDI_CHAN_PREFIX, [2], delta)
track.events << MetaEvent.new(META_SMPTE, [12, 30, 12, 11, 5], delta)
track.events << MetaEvent.new(META_SEQ_SPECIF, [12, 22, -4], delta)

# track.events << MetaEvent.new(META_TRACK_END, [], delta)

File.open('test-meta.mid', 'wb') { | file | seq.write(file) }