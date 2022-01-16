package run.tere.plugin.icerush.wrapped.nbapi;

import com.xxmicloxx.NoteBlockAPI.model.CustomInstrument;
import com.xxmicloxx.NoteBlockAPI.model.Layer;
import com.xxmicloxx.NoteBlockAPI.model.Note;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.CompatibilityUtils;
import com.xxmicloxx.NoteBlockAPI.utils.InstrumentUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WrappedNBSDecoder {

    public static Song parse(File songFile) {
        try {
            return parse(new FileInputStream(songFile), songFile);
        } catch (FileNotFoundException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static WrappedSong parse(InputStream inputStream, File songFile) {
        HashMap<Integer, Layer> layerHashMap = new HashMap();
        boolean isStereo = false;

        try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            short length = readShort(dataInputStream);
            int firstcustominstrument = 10;
            int nbsversion = 0;
            if (length == 0) {
                nbsversion = dataInputStream.readByte();
                firstcustominstrument = dataInputStream.readByte();
                if (nbsversion >= 3) {
                    length = readShort(dataInputStream);
                }
            }

            int firstcustominstrumentdiff = InstrumentUtils.getCustomInstrumentFirstIndex() - firstcustominstrument;
            short songHeight = readShort(dataInputStream);
            String title = readString(dataInputStream);
            String author = readString(dataInputStream);
            String originalAuthor = readString(dataInputStream);
            String description = readString(dataInputStream);
            float speed = (float)readShort(dataInputStream) / 100.0F;
            dataInputStream.readBoolean();
            dataInputStream.readByte();
            dataInputStream.readByte();
            readInt(dataInputStream);
            readInt(dataInputStream);
            readInt(dataInputStream);
            readInt(dataInputStream);
            readInt(dataInputStream);
            readString(dataInputStream);
            short loopStartTick = 0;
            if (nbsversion >= 4) {
                dataInputStream.readByte();
                dataInputStream.readByte();
                loopStartTick = readShort(dataInputStream);
            }

            short tick = -1;

            while(true) {
                short jumpTicks = readShort(dataInputStream);
                byte volume;
                if (jumpTicks == 0) {
                    if (nbsversion > 0 && nbsversion < 3) {
                        length = tick;
                    }

                    for(int i = 0; i < songHeight; ++i) {
                        Layer layer = layerHashMap.get(i);
                        String name = readString(dataInputStream);
                        if (nbsversion >= 4) {
                            dataInputStream.readByte();
                        }

                        volume = dataInputStream.readByte();
                        int panning = 100;
                        if (nbsversion >= 2) {
                            panning = 200 - dataInputStream.readUnsignedByte();
                        }

                        if (panning != 100) {
                            isStereo = true;
                        }

                        if (layer != null) {
                            layer.setName(name);
                            layer.setVolume(volume);
                            layer.setPanning(panning);
                        }
                    }

                    byte customAmnt = dataInputStream.readByte();
                    CustomInstrument[] customInstrumentsArray = new CustomInstrument[customAmnt];

                    for(int index = 0; index < customAmnt; ++index) {
                        customInstrumentsArray[index] = new CustomInstrument((byte)index, readString(dataInputStream), readString(dataInputStream));
                        dataInputStream.readByte();
                        dataInputStream.readByte();
                    }

                    if (firstcustominstrumentdiff < 0) {
                        ArrayList<CustomInstrument> customInstruments = CompatibilityUtils.getVersionCustomInstrumentsForSong(firstcustominstrument);
                        customInstruments.addAll(Arrays.asList(customInstrumentsArray));
                        customInstrumentsArray = (CustomInstrument[])customInstruments.toArray(customInstrumentsArray);
                    } else {
                        firstcustominstrument += firstcustominstrumentdiff;
                    }

                    return new WrappedSong(loopStartTick, speed, layerHashMap, songHeight, length, title, author, originalAuthor, description, songFile, firstcustominstrument, customInstrumentsArray, isStereo);
                }

                tick += jumpTicks;
                short layer = -1;

                while(true) {
                    short jumpLayers = readShort(dataInputStream);
                    if (jumpLayers == 0) {
                        break;
                    }

                    layer += jumpLayers;
                    volume = dataInputStream.readByte();
                    if (firstcustominstrumentdiff > 0 && volume >= firstcustominstrument) {
                        volume = (byte)(volume + firstcustominstrumentdiff);
                    }

                    byte key = dataInputStream.readByte();
                    byte velocity = 100;
                    int panning = 100;
                    short pitch = 0;
                    if (nbsversion >= 4) {
                        velocity = dataInputStream.readByte();
                        panning = 200 - dataInputStream.readUnsignedByte();
                        pitch = readShort(dataInputStream);
                    }

                    if (panning != 100) {
                        isStereo = true;
                    }

                    setNote(layer, tick, new Note(volume, key, velocity, panning, pitch), layerHashMap);
                }
            }
        } catch (EOFException var26) {
            String file = "";
            if (songFile != null) {
                file = songFile.getName();
            }

            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Song is corrupted: " + file);
        } catch (IOException var25) {
            var25.printStackTrace();
        }

        return null;
    }

    private static void setNote(int layerIndex, int ticks, Note note, HashMap<Integer, Layer> layerHashMap) {
        Layer layer = layerHashMap.get(layerIndex);
        if (layer == null) {
            layer = new Layer();
            layerHashMap.put(layerIndex, layer);
        }

        layer.setNote(ticks, note);
    }

    private static short readShort(DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        return (short)(byte1 + (byte2 << 8));
    }

    private static int readInt(DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        int byte3 = dataInputStream.readUnsignedByte();
        int byte4 = dataInputStream.readUnsignedByte();
        return byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24);
    }

    private static String readString(DataInputStream dataInputStream) throws IOException {
        int length = readInt(dataInputStream);

        StringBuilder builder;
        for(builder = new StringBuilder(length); length > 0; --length) {
            char c = (char)dataInputStream.readByte();
            if (c == '\r') {
                c = ' ';
            }

            builder.append(c);
        }

        return builder.toString();
    }

}
