package com.huaban.analysis.jieba;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class Jieba {
    static public class StrSeg {
        public String seg;
        public int    type;

        public StrSeg(String s, int t) {
            seg = s;
            type = t;
        }

    }

    public static int codePointMode(int n) {
        return n < 128 ? 0 : 1;
    }

    public static List<StrSeg> splitSeg(String str) {
        List<StrSeg>   segs = new ArrayList<StrSeg>();
        int               n = str.length();
        if(n<=0) {
            return segs;
        }

        StringBuilder sb = new StringBuilder();
        int mode=codePointMode(str.codePointAt(0));

        for(int i=0; i<n; ++i) {
            int curMode = codePointMode(str.codePointAt(i));
            if(mode == curMode) {
                sb.append(str.charAt(i));
            } else {
                segs.add(new StrSeg(sb.toString(), mode));

                sb   = new StringBuilder(str.charAt(i));
                mode = curMode;
            }
        }
        return segs;
    }

    public static List<SegToken> mixedTokenize(JiebaSegmenter segmenter, String str, JiebaSegmenter.SegMode mode) {
        List<SegToken>  result      = new ArrayList<SegToken>();
        int             startOffset = 0;

        for(StrSeg seg : splitSeg(str)) {
            if(seg.type == 0) {
                result.add(new SegToken(seg.seg, startOffset, startOffset + seg.seg.length()));
            } else {
                List<SegToken> curSegments = tokenize(segmenter, seg.seg, mode);
                result.addAll(curSegments);
            }

            startOffset += seg.seg.length();
        }
        return result;
    }

    public static boolean isAscii(String s) {
        for(int i=0; i<s.length(); ++i) {
            if(s.codePointAt(i) > 127) {
                return false;
            }
        }
        return true;
    }

    public static List<SegToken> tokenize(JiebaSegmenter segmenter, String str, JiebaSegmenter.SegMode mode) {
        List<SegToken> stoken = segmenter.process(str, mode);
        return stoken;
    }

    public static void main(String args[]) {
        JiebaSegmenter segmenter    = new JiebaSegmenter();
        JiebaSegmenter.SegMode mode = JiebaSegmenter.SegMode.INDEX;
        int mixedMode               = 1;
        if(System.getProperty("jieba.segmode", "index").toLowerCase(Locale.ENGLISH).equals("search")) {
            System.err.println("search mode");
            mode = JiebaSegmenter.SegMode.SEARCH;
        } else {
            System.err.println("index mode");
        }
        if(System.getProperty("jieba.mixedmode", "1").toLowerCase(Locale.ENGLISH).equals("0")) {
            mixedMode = 0;
            System.err.println("direct mode");
        } else {
            System.err.println("mixed mode");
        }

       

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            while(true) {
                String str = br.readLine();
                if(str==null) { break; }

                List<SegToken> stoken = null;
                if(mixedMode == 1) {
                    stoken = mixedTokenize(segmenter, str, mode);
                } else {
                    stoken = tokenize(segmenter, str, mode);
                }

                StringBuilder sb = new StringBuilder();
                for(SegToken token : stoken) {
                    sb.append(token.word);
                    if(!isAscii(token.word)) {
                        sb.append(" ");
                    }
                }
                System.out.println(sb.toString().trim());

            }
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
}
