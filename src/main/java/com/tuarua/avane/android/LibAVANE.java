package com.tuarua.avane.android;

import android.util.Log;

import com.tuarua.avane.android.gets.AvailableFormat;
import com.tuarua.avane.android.gets.BitStreamFilter;
import com.tuarua.avane.android.gets.Codec;
import com.tuarua.avane.android.gets.Color;
import com.tuarua.avane.android.gets.Decoder;
import com.tuarua.avane.android.gets.Device;
import com.tuarua.avane.android.gets.Encoder;
import com.tuarua.avane.android.gets.Filter;
import com.tuarua.avane.android.gets.HardwareAcceleration;
import com.tuarua.avane.android.gets.Layout;
import com.tuarua.avane.android.gets.Layouts;
import com.tuarua.avane.android.gets.PixelFormat;
import com.tuarua.avane.android.gets.Protocol;
import com.tuarua.avane.android.gets.Protocols;
import com.tuarua.avane.android.gets.SampleFormat;
import com.tuarua.avane.android.probe.AudioStream;
import com.tuarua.avane.android.probe.Probe;
import com.tuarua.avane.android.probe.SubtitleStream;
import com.tuarua.avane.android.probe.VideoStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by User on 02/10/2016.
 */
public class LibAVANE {
    public JniEventDispatcher eventDispatcher = JniEventDispatcher.getInstance();
    private static LibAVANE ourInstance = new LibAVANE();
    public static LibAVANE getInstance() {
        return ourInstance;
    }

    private LibAVANE() {
    }

    private String[] cliParse(String str, Boolean lookForQuotes) {
        String[] args;
        List<String> argsList = new ArrayList<String>();
        Boolean readingPart = false;
        String part = "";
        for (int i = 0; i < str.length(); i++) {
            String s = String.valueOf(str.charAt(i));
            if (s.equals(" ") && !readingPart){
                argsList.add(part);
                part = "";
            }else{
                if(s.equals("\\") && lookForQuotes)
                    readingPart = !readingPart;
                else
                    part +=s ;
            }
        }
        argsList.add(part);
        args = argsList.toArray(new String[argsList.size()]);
        return args;
    }


    public void triggerProbeInfo(String filename) {
        jni_triggerProbeInfo(filename);
    }

    public Probe getProbeInfo() {
        String json = jni_getProbeInfo();
        JSONObject jsonProbe;
        JSONObject jsonFormat;
        JSONArray videoStreams;
        JSONArray audioStreams;
        JSONArray subtitleStreams;
        Probe probe = new Probe();

        try {
            jsonProbe = new JSONObject(json);
            jsonFormat = jsonProbe.getJSONObject("format");

            probe.format.bitRate = jsonFormat.getInt("bitRate");
            probe.format.duration = jsonFormat.getDouble("duration");
            probe.format.filename = jsonFormat.getString("filename");
            probe.format.numStreams = jsonFormat.getInt("numStreams");
            probe.format.numPrograms = jsonFormat.getInt("numPrograms");
            probe.format.formatName = jsonFormat.getString("formatName");
            probe.format.formatLongName = jsonFormat.getString("formatLongName");
            probe.format.startTime = jsonFormat.getDouble("startTime");
            probe.format.size = jsonFormat.getInt("size");
            probe.format.probeScore = jsonFormat.getInt("probeScore");

            JSONObject jsonTags = jsonFormat.getJSONObject("tags");
            Iterator<String> iter = jsonTags.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = jsonTags.get(key);
                    probe.format.tags.put(key, (String) value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(jsonProbe.has("videoStreams")){
                videoStreams = jsonProbe.getJSONArray("videoStreams");
                JSONObject jsonStream;
                VideoStream videoStream = new VideoStream();
                for(int i=0;i < videoStreams.length();i++) {
                    jsonStream = videoStreams.getJSONObject(i);
                    if(jsonStream.has("averageFrameRate"))
                        videoStream.averageFrameRate = jsonStream.getDouble("averageFrameRate");
                    if(jsonStream.has("bitRate"))
                        videoStream.bitRate = jsonStream.getDouble("bitRate");
                    if(jsonStream.has("bitsPerRawSample"))
                        videoStream.bitsPerRawSample = jsonStream.getDouble("bitsPerRawSample");
                    if(jsonStream.has("chromaLocation"))
                        videoStream.chromaLocation = jsonStream.getString("chromaLocation");
                    videoStream.codedHeight = jsonStream.getInt("codedHeight");
                    videoStream.codedWidth = jsonStream.getInt("codedWidth");
                    if(jsonStream.has("colorPrimaries"))
                        videoStream.colorPrimaries = jsonStream.getString("colorPrimaries");
                    if(jsonStream.has("colorRange"))
                        videoStream.colorRange = jsonStream.getString("colorRange");
                    if(jsonStream.has("colorSpace"))
                        videoStream.colorSpace = jsonStream.getString("colorSpace");
                    if(jsonStream.has("colorTransfer"))
                        videoStream.colorTransfer = jsonStream.getString("colorTransfer");
                    videoStream.codecLongName = jsonStream.getString("codecLongName");
                    videoStream.codecName = jsonStream.getString("codecName");
                    videoStream.codecTag = jsonStream.getInt("codecTag");
                    videoStream.codecTagString = jsonStream.getString("codecTagString");
                    videoStream.codecTimeBase = jsonStream.getString("codecTimeBase");
                    videoStream.codecType = jsonStream.getString("codecType");
                    if(jsonStream.has("displayAspectRatio"))
                        videoStream.displayAspectRatio = jsonStream.getString("displayAspectRatio");
                    if(jsonStream.has("duration"))
                        videoStream.duration = jsonStream.getDouble("duration");
                    if(jsonStream.has("durationTimestamp"))
                        videoStream.durationTimestamp = jsonStream.getDouble("durationTimestamp");
                    videoStream.hasBframes = jsonStream.getInt("hasBframes");
                    videoStream.height = jsonStream.getInt("height");
                    if(jsonStream.has("id"))
                        videoStream.id = jsonStream.getString("id");
                    videoStream.index = jsonStream.getInt("index");
                    videoStream.level = jsonStream.getInt("level");
                    if(jsonStream.has("maxBitRate"))
                        videoStream.maxBitRate = jsonStream.getDouble("maxBitRate");
                    if(jsonStream.has("numFrames"))
                        videoStream.numFrames = jsonStream.getDouble("numFrames");
                    videoStream.pixelFormat = jsonStream.getString("pixelFormat");
                    videoStream.profile = jsonStream.getString("profile");
                    if(jsonStream.has("realFrameRate"))
                        videoStream.realFrameRate = jsonStream.getDouble("realFrameRate");
                    videoStream.refs = jsonStream.getInt("refs");
                    if(jsonStream.has("sampleAspectRatio"))
                        videoStream.sampleAspectRatio = jsonStream.getString("sampleAspectRatio");
                    videoStream.startPTS = jsonStream.getDouble("startPTS");
                    videoStream.startTime = jsonStream.getDouble("startTime");
                    if(jsonFormat.has("tags")) {
                        JSONObject jsonTagsV = jsonFormat.getJSONObject("tags");
                        Iterator<String> iter2 = jsonTagsV.keys();
                        while (iter.hasNext()) {
                            String key = iter2.next();
                            try {
                                Object value = jsonTagsV.get(key);
                                videoStream.tags.put(key, (String) value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    videoStream.timecode = jsonStream.getString("timecode");
                    videoStream.timeBase = jsonStream.getString("timeBase");
                    videoStream.width = jsonStream.getInt("width");

                    probe.videoStreams.add(videoStream);
                }
            }


            if(jsonProbe.has("audioStreams")) {
                audioStreams = jsonProbe.getJSONArray("audioStreams");
                JSONObject jsonStream;
                AudioStream audioStream = new AudioStream();

                for(int i=0;i < audioStreams.length();i++) {
                    jsonStream = audioStreams.getJSONObject(i);
                    if(jsonStream.has("averageFrameRate"))
                        audioStream.averageFrameRate = jsonStream.getDouble("averageFrameRate");
                    if(jsonStream.has("bitRate"))
                        audioStream.bitRate = jsonStream.getDouble("bitRate");
                    if(jsonStream.has("bitsPerRawSample"))
                        audioStream.bitsPerRawSample = jsonStream.getDouble("bitsPerRawSample");
                    audioStream.codecLongName = jsonStream.getString("codecLongName");
                    audioStream.codecName = jsonStream.getString("codecName");
                    audioStream.codecTag = jsonStream.getInt("codecTag");
                    audioStream.codecTagString = jsonStream.getString("codecTagString");
                    audioStream.codecTimeBase = jsonStream.getString("codecTimeBase");
                    audioStream.codecType = jsonStream.getString("codecType");

                    if(jsonStream.has("duration"))
                        audioStream.duration = jsonStream.getDouble("duration");
                    if(jsonStream.has("durationTimestamp"))
                        audioStream.durationTimestamp = jsonStream.getDouble("durationTimestamp");

                    if(jsonStream.has("id"))
                        audioStream.id = jsonStream.getString("id");
                    audioStream.index = jsonStream.getInt("index");
                    if(jsonStream.has("maxBitRate"))
                        audioStream.maxBitRate = jsonStream.getDouble("maxBitRate");
                    if(jsonStream.has("numFrames"))
                        audioStream.numFrames = jsonStream.getDouble("numFrames");
                    audioStream.profile = jsonStream.getString("profile");
                    if(jsonStream.has("realFrameRate"))
                        audioStream.realFrameRate = jsonStream.getDouble("realFrameRate");
                    audioStream.startPTS = jsonStream.getDouble("startPTS");
                    audioStream.startTime = jsonStream.getDouble("startTime");

                    if(jsonFormat.has("tags")) {
                        JSONObject jsonTagsV = jsonFormat.getJSONObject("tags");
                        Iterator<String> iter2 = jsonTagsV.keys();
                        while (iter.hasNext()) {
                            String key = iter2.next();
                            try {
                                Object value = jsonTagsV.get(key);
                                audioStream.tags.put(key, (String) value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    audioStream.timeBase = jsonStream.getString("timeBase");
                    audioStream.sampleFormat = jsonStream.getString("sampleFormat");
                    audioStream.sampleRate = jsonStream.getInt("sampleRate");
                    audioStream.channels = jsonStream.getInt("channels");
                    audioStream.channelLayout = jsonStream.getString("channelLayout");
                    audioStream.bitsPerSample = jsonStream.getInt("bitsPerSample");

                    probe.audioStreams.add(audioStream);
                }
            }

            if(jsonProbe.has("subtitleStreams")) {
                subtitleStreams = jsonProbe.getJSONArray("subtitleStreams");
                JSONObject jsonStream;
                SubtitleStream subtitleStream = new SubtitleStream();

                for(int i=0;i < subtitleStreams.length();i++) {
                    jsonStream = subtitleStreams.getJSONObject(i);
                    if(jsonStream.has("averageFrameRate"))
                        subtitleStream.averageFrameRate = jsonStream.getDouble("averageFrameRate");
                    if(jsonStream.has("bitRate"))
                        subtitleStream.bitRate = jsonStream.getDouble("bitRate");
                    if(jsonStream.has("bitsPerRawSample"))
                        subtitleStream.bitsPerRawSample = jsonStream.getDouble("bitsPerRawSample");
                    subtitleStream.codecLongName = jsonStream.getString("codecLongName");
                    subtitleStream.codecName = jsonStream.getString("codecName");
                    subtitleStream.codecTag = jsonStream.getInt("codecTag");
                    subtitleStream.codecTagString = jsonStream.getString("codecTagString");
                    subtitleStream.codecTimeBase = jsonStream.getString("codecTimeBase");
                    subtitleStream.codecType = jsonStream.getString("codecType");

                    if(jsonStream.has("duration"))
                        subtitleStream.duration = jsonStream.getDouble("duration");
                    if(jsonStream.has("durationTimestamp"))
                        subtitleStream.durationTimestamp = jsonStream.getDouble("durationTimestamp");

                    if(jsonStream.has("id"))
                        subtitleStream.id = jsonStream.getString("id");
                    subtitleStream.index = jsonStream.getInt("index");
                    if(jsonStream.has("maxBitRate"))
                        subtitleStream.maxBitRate = jsonStream.getDouble("maxBitRate");
                    if(jsonStream.has("numFrames"))
                        subtitleStream.numFrames = jsonStream.getDouble("numFrames");
                    subtitleStream.profile = jsonStream.getString("profile");
                    if(jsonStream.has("realFrameRate"))
                        subtitleStream.realFrameRate = jsonStream.getDouble("realFrameRate");
                    subtitleStream.startPTS = jsonStream.getDouble("startPTS");
                    subtitleStream.startTime = jsonStream.getDouble("startTime");

                    if(jsonFormat.has("tags")){
                        JSONObject jsonTagsV = jsonFormat.getJSONObject("tags");
                        Iterator<String> iter2 = jsonTagsV.keys();
                        while (iter.hasNext()) {
                            String key = iter2.next();
                            try {
                                Object value = jsonTagsV.get(key);
                                subtitleStream.tags.put(key, (String) value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    subtitleStream.timeBase = jsonStream.getString("timeBase");
                    subtitleStream.width = jsonStream.getInt("width");
                    subtitleStream.height = jsonStream.getInt("height");

                    probe.subtitleStreams.add(subtitleStream);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return probe;

    }

    public ArrayList<Filter> getFilters(){
        ArrayList<Filter> vecFilters = new ArrayList<>();
        String json = jni_getFilters();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                Filter fltr;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    fltr = new Filter();
                    fltr.description = jsonObject.getString("d");
                    fltr.hasCommandSupport = jsonObject.getBoolean("hcs");
                    fltr.hasSliceThreading = (jsonObject.getInt("hst") == 1);
                    fltr.hasTimelineSupport = (jsonObject.getInt("hts") == 1);
                    fltr.name = jsonObject.getString("n");
                    fltr.type = jsonObject.getString("t");
                    vecFilters.add(i, fltr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecFilters;
    }

    public ArrayList<PixelFormat> getPixelFormats() {
        ArrayList<PixelFormat> vecFormats = new ArrayList<>();
        String json = jni_getPixelFormats();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                PixelFormat pixFrmt;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    pixFrmt = new PixelFormat();
                    pixFrmt.bitsPerPixel = jsonObject.getInt("bitsPerPixel");
                    pixFrmt.name = jsonObject.getString("name");
                    pixFrmt.isInput = jsonObject.getBoolean("isInput");
                    pixFrmt.isOutput = jsonObject.getBoolean("isOutput");
                    pixFrmt.isHardwareAccelerated = jsonObject.getBoolean("isHardwareAccelerated");
                    pixFrmt.isPalleted = jsonObject.getBoolean("isPalleted");
                    pixFrmt.isBitStream = jsonObject.getBoolean("isBitStream");
                    pixFrmt.numComponents = jsonObject.getInt("numComponents");
                    vecFormats.add(i, pixFrmt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecFormats;
    }
    public Layouts getLayouts() {
        Layouts layouts = new Layouts();
        String json = jni_getLayouts();
        JSONObject jsonObject;
        if(json != null && !json.isEmpty()) {
            try {
                jsonObject = new JSONObject(json);
                JSONArray jsonArrayI = (JSONArray) jsonObject.get("individual");
                JSONArray jsonArrayS = (JSONArray) jsonObject.get("standard");
                JSONObject jsonObjectI;
                JSONObject jsonObjectS;
                Layout lyout;
                for (int i = 0; i < jsonArrayI.length(); i++) {
                    jsonObjectI = jsonArrayI.getJSONObject(i);
                    lyout = new Layout();
                    lyout.name = jsonObjectI.getString("n");
                    lyout.description = jsonObjectI.getString("d");
                    layouts.individual.add(i, lyout);
                }

                for (int i = 0; i < jsonArrayS.length(); i++) {
                    jsonObjectS = jsonArrayS.getJSONObject(i);
                    lyout = new Layout();
                    lyout.name = jsonObjectS.getString("n");
                    lyout.description = jsonObjectS.getString("d");
                    layouts.standard.add(i, lyout);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return layouts;
    }
    public ArrayList<Color> getColors() {
        ArrayList<Color> vecColors = new ArrayList<>();
        String json = jni_getColors();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                Color clr;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    clr = new Color();
                    clr.name = jsonObject.getString("n");
                    clr.value = jsonObject.getString("v");
                    vecColors.add(i, clr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecColors;
    }

    public Protocols getProtocols() {
        Protocols protocols = new Protocols();
        String json = jni_getProtocols();

        if(json != null && !json.isEmpty()) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(json);
                JSONArray jsonArrayI = (JSONArray) jsonObject.get("i");
                JSONArray jsonArrayO = (JSONArray) jsonObject.get("o");
                JSONObject jsonObjectI;
                JSONObject jsonObjectO;

                Protocol protocol;
                for (int i = 0; i < jsonArrayI.length(); i++) {
                    jsonObjectI = jsonArrayI.getJSONObject(i);
                    protocol = new Protocol();
                    protocol.name = jsonObjectI.getString("n");
                    protocols.inputs.add(i, protocol);
                }

                for (int i = 0; i < jsonArrayO.length(); i++) {
                    jsonObjectO = jsonArrayO.getJSONObject(i);
                    protocol = new Protocol();
                    protocol.name = jsonObjectO.getString("n");
                    protocols.outputs.add(i, protocol);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return protocols;
    }

    public ArrayList<BitStreamFilter> getBitStreamFilters() {
        ArrayList<BitStreamFilter> vecBsfs = new ArrayList<>();
        String json = jni_getBitStreamFilters();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                BitStreamFilter bsf;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    bsf = new BitStreamFilter();
                    bsf.name = jsonObject.getString("n");
                    vecBsfs.add(i, bsf);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecBsfs;
    }

    public ArrayList<Codec> getCodecs() {
        ArrayList<Codec> vecCodecs = new ArrayList<>();
        String json = jni_getCodecs();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                Codec codec;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    codec = new Codec();
                    codec.name = jsonObject.getString("n");
                    codec.nameLong = jsonObject.getString("nl");
                    codec.hasDecoder = jsonObject.getBoolean("d");
                    codec.hasEncoder = jsonObject.getBoolean("e");

                    if (jsonObject.has("v"))
                        codec.isVideo = jsonObject.getBoolean("v");
                    if (jsonObject.has("a"))
                        codec.isAudio = jsonObject.getBoolean("a");
                    if (jsonObject.has("s"))
                        codec.isSubtitles = jsonObject.getBoolean("s");
                    if (jsonObject.has("ly"))
                        codec.isLossy = jsonObject.getBoolean("ly");
                    if (jsonObject.has("ll"))
                        codec.isLossless = jsonObject.getBoolean("ll");
                    if (jsonObject.has("in"))
                        codec.isIntraFrameOnly = jsonObject.getBoolean("in");

                    vecCodecs.add(i, codec);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecCodecs;
    }

    public ArrayList<Decoder> getDecoders() {
        ArrayList<Decoder> vecDecoders = new ArrayList<>();
        String json = jni_getDecoders();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                Decoder decoder;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    decoder = new Decoder();
                    decoder.name = jsonObject.getString("n");
                    decoder.nameLong = jsonObject.getString("nl");
                    if (jsonObject.has("v"))
                        decoder.isVideo = jsonObject.getBoolean("v");
                    if (jsonObject.has("a"))
                        decoder.isAudio = jsonObject.getBoolean("a");
                    if (jsonObject.has("s"))
                        decoder.isSubtitles = jsonObject.getBoolean("s");
                    if (jsonObject.has("flm"))
                        decoder.hasFrameLevelMultiThreading = jsonObject.getBoolean("flm");
                    if (jsonObject.has("slm"))
                        decoder.hasSliceLevelMultiThreading = jsonObject.getBoolean("slm");
                    if (jsonObject.has("ex"))
                        decoder.isExperimental = jsonObject.getBoolean("ex");
                    if (jsonObject.has("hb"))
                        decoder.supportsDrawHorizBand = jsonObject.getBoolean("hb");
                    if (jsonObject.has("dr"))
                        decoder.supportsDirectRendering = jsonObject.getBoolean("dr");
                    vecDecoders.add(i, decoder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return vecDecoders;
    }

    public ArrayList<Encoder> getEncoders() {
        ArrayList<Encoder> vecEncoders = new ArrayList<>();
        String json = jni_getEncoders();
        if(json != null && !json.isEmpty()){
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                Encoder encoder;
                for(int i=0;i < jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    encoder = new Encoder();
                    encoder.name = jsonObject.getString("n");
                    encoder.nameLong = jsonObject.getString("nl");
                    if(jsonObject.has("v"))
                        encoder.isVideo = jsonObject.getBoolean("v");
                    if(jsonObject.has("a"))
                        encoder.isAudio = jsonObject.getBoolean("a");
                    if(jsonObject.has("s"))
                        encoder.isSubtitles = jsonObject.getBoolean("s");
                    if(jsonObject.has("flm"))
                        encoder.hasFrameLevelMultiThreading = jsonObject.getBoolean("flm");
                    if(jsonObject.has("slm"))
                        encoder.hasSliceLevelMultiThreading = jsonObject.getBoolean("slm");
                    if(jsonObject.has("ex"))
                        encoder.isExperimental = jsonObject.getBoolean("ex");
                    if(jsonObject.has("hb"))
                        encoder.supportsDrawHorizBand = jsonObject.getBoolean("hb");
                    if(jsonObject.has("dr"))
                        encoder.supportsDirectRendering = jsonObject.getBoolean("dr");
                    vecEncoders.add(i,encoder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecEncoders;
    }

    public ArrayList<HardwareAcceleration> getHardwareAccelerations(){
        ArrayList<HardwareAcceleration> vecHW = new ArrayList<>();
        String json = jni_getHardwareAccelerations();
        if(json != null && !json.isEmpty()){
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                HardwareAcceleration hw;
                for(int i=0;i < jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    hw = new HardwareAcceleration();
                    hw.name = jsonObject.getString("n");
                    vecHW.add(i,hw);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecHW;
    }

    public ArrayList<Device> getDevices() {
        ArrayList<Device> vecDevices = new ArrayList<>();
        String json = jni_getDevices();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                Device device;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    device = new Device();
                    device.name = jsonObject.getString("n");
                    device.nameLong = jsonObject.getString("nl");
                    device.demuxing = jsonObject.getBoolean("d");
                    device.muxing = jsonObject.getBoolean("m");
                    vecDevices.add(i, device);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecDevices;
    }

    public ArrayList<AvailableFormat> getAvailableFormats(){
        ArrayList<AvailableFormat> vecFormats = new ArrayList<>();
        String json = jni_getAvailableFormats();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                AvailableFormat availableFormat;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    availableFormat = new AvailableFormat();
                    availableFormat.name = jsonObject.getString("n");
                    availableFormat.nameLong = jsonObject.getString("nl");
                    availableFormat.demuxing = jsonObject.getBoolean("d");
                    availableFormat.muxing = jsonObject.getBoolean("m");
                    vecFormats.add(i, availableFormat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecFormats;
    }

    public ArrayList<SampleFormat> getSampleFormats() {
        ArrayList<SampleFormat> vecFormats = new ArrayList<>();
        String json = jni_getSampleFormats();
        if(json != null && !json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                SampleFormat sampleFormat;
                for (int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.isNull(i)) continue;
                    jsonObject = jsonArray.getJSONObject(i);
                    sampleFormat = new SampleFormat();
                    sampleFormat.name = jsonObject.getString("n");
                    sampleFormat.depth = jsonObject.getString("d");
                    vecFormats.add(sampleFormat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vecFormats;
    }

    public String getLicense() {
        return jni_getLicense();
    }

    public String getVersion() {
        return jni_getVersion();
    }
    public String getBuildConfiguration() {
        return jni_getBuildConfiguration();
    }

    public void encode(String[] path) {
        jni_encode(path);
    }
    public void encode(String path) {
        jni_encode(cliParse(path,true));
    }

    public void setLogLevel(int level) {
        jni_setLogLevel(level);
    }

    public Boolean cancelEncode() {
        jni_cancelEncode();
        return true;
    }

    public Boolean pauseEncode(Boolean value) {
        jni_pauseEncode(value);
        return true;
    }
    private native void jni_triggerProbeInfo(String filename);
    private native String jni_getProbeInfo();
    private native String jni_getFilters();
    private native String jni_getPixelFormats();
    private native String jni_getLayouts();
    private native String jni_getVersion();
    private native String jni_getColors();
    private native String jni_getProtocols();
    private native String jni_getLicense();
    private native String jni_getBuildConfiguration();
    private native String jni_getHardwareAccelerations();
    private native String jni_getDevices();
    private native String jni_getAvailableFormats();
    private native String jni_getSampleFormats();
    private native String jni_getBitStreamFilters();
    private native String jni_getCodecs();
    private native String jni_getDecoders();
    private native String jni_getEncoders();
    private native void jni_encode(String[] path);
    private native void jni_setLogLevel(int level);
    private native void jni_cancelEncode();
    private native void jni_pauseEncode(Boolean value);
    static {
        System.loadLibrary("avane-lib");
    }
}
