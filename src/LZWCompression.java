/**
 * A simple compress and decompress program using LZW algorithm, supports all kinds
 * of binary files.
 *
 * In order to execute the program for compression the user will type the following:
 * java LZWCompression -c shortwords.txt zippedFile.txt
 * And to decompress the program is run with the following command:
 * java LZWCompression -d zippedFile.txt unzippedFile.txt
 * The user may also decide to show verbose output with the –v switch.
 * The following commands shows the total number of bytes read and the total number
 * of bytes written as the the program compresses and decompresses the file:
 * java LZWCompression -c –v shortwords.txt zippedFile.txt
 * java LZWCompression -d –v zippedFile.txt unzippedFile.txt
 *
 * @author Maiqi Ding, maiqid@andrew.cmu.edu
 * @version Nov 21, 2019
 */

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class LZWCompression {

    /* Store the input binary file */
    private LinkedList<Character> input;
    /* Temporary compress result list */
    private LinkedList<Integer> temp;
    public static final int MAXKEY = 4096;

    LZWCompression() {
        temp = new LinkedList<>();
    }


    /**
     * Load byte from the input file and store it into Char ArrayList
     * In this way, the program can work on both ASCII files and binary files
     *
     * @param inputfile input file name
     * @throws IOException
     */
    public void load(String inputfile) throws IOException {
        input = new LinkedList<>();
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputfile)));
        byte byteIn;
        try {
            while (true) {
                byteIn = in.readByte();
                input.add((char) (byteIn & 0XFF));
            }
        } catch (EOFException e) {
            in.close();
        }
    }


    /**
     * Compress method, compress the original input and then write the
     * result into file
     * rebuild the hashMap when the combination exceeds 4096
     */
    public void Compress(String outputfile, boolean verbose) throws IOException {

        int j = 256;
        //enter all symbols in the table
        hashMap<String, Integer> map = new hashMap<>(10000);
        for (int i = 0; i < 256; i++) {
            map.put(Character.toString((char) i), i);
        }
        int total = input.size();

        //read first character from w into string s
        String s = Character.toString(input.remove(0));
        while (input.size() != 0) {
            String c = Character.toString(input.remove(0));
            if (map.containsKey(s + c)) {
                s = s + c;
            } else {

                temp.add(map.get(s));
                if (map.get(s) == null) {
                    System.out.println(s);
                }
                map.put(s + c, j);
                s = c;
                j++;
                if (j == MAXKEY) {
                    map = new hashMap<>(10000);
                    for (int i = 0; i < 256; i++) {
                        map.put(Character.toString((char) i), i);
                    }
                    j = 256;
                }
            }
        }

        temp.add(map.get(s));

        /* write to file*/
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputfile)));
        LinkedList<Character> result = transformFrom12to8(temp);
        for (char c : result) {
            out.writeByte(c);
        }
        out.close();

        if (verbose) {
            System.out.println("bytes read = " + total + ", bytes written = " + result.size());
        }
    }


    /**
     * Convert the integer list to character list, take the least 12 bits of each integer
     * and rearrange the order to form the 8bit character list
     *
     * @param list1 Integer list after compress
     * @return Character List
     */
    public LinkedList<Character> transformFrom12to8(LinkedList<Integer> list1) {
        ArrayList<Integer> list = new ArrayList<>(list1);
        LinkedList<Character> result = new LinkedList<>();
        int mask = 0X00000FFF;//last 12 bit
        int mask_front_4 = 0X00000F00;
        int mask_8 = 0X00000FF0;//5-12 bits
        int mask_4 = 0X0000000F;// last 4 bits
        int mask_last_8 = 0X000000FF;//last 8 bits


        int evenback = 0;
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 0) {// 0 ,2,4,6,8 first 8 bit into char, last bit into next char
                int var = list.get(i);
                var = var & mask;//12 bits
                int front = (var & mask_8) >> 4;//front 8bits
                evenback = var & mask_4;//back 4 bits
                result.add((char) front);
                result.add((char) (evenback << 4));
            } else {
                int var = list.get(i);
                var = var & mask;//12 bits
                evenback = evenback << 4;
                int front4 = (var & mask_front_4) >> 8;
                int last8 = var & mask_last_8;
                int temp = evenback + front4;
                result.removeLast();
                result.add((char) temp);
                result.add((char) last8);
            }
        }
        return result;
    }


    /**
     * Convert the character list to integer list, rearrange the order to form the 12bit Integer list
     *
     * @param list1 original input when decompress
     * @return List of Integer
     */
    public LinkedList<Integer> transformFrom8to12(LinkedList<Character> list1) {
        ArrayList<Character> list = new ArrayList<>(list1);
        int mask1 = 0X000000F0;//first4bits
        int mask2 = 0X0000000F;//last4bits

        int j = 0;
        LinkedList<Integer> result = new LinkedList<>();
        ArrayList<Character> mid = new ArrayList<>();

        //Store the middle line separately
        for (int i = 0; i < list.size(); i++) {//list.size()-1 , need next line
            if ((i - 1) % 3 == 0) {
                char var = list.get(i);
                char front4 = (char) (((int) var & mask1) >>> 4);
                char back4 = (char) (var & mask2);
                mid.add(front4);
                mid.add(back4);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            if (i % 3 == 0) {
                char var = list.get(i);
                char temp1 = (char) (((int) var) << 4);
                char temp2 = mid.get(j);
                char temp = (char) ((int) temp1 + (int) temp2);
                result.add((int) temp);
                j++;
            }

            if ((i - 2) % 3 == 0) {
                char var = list.get(i);
                char temp1 = (char) ((int) mid.get(j) << 8);
                char temp = (char) ((int) var + (int) temp1);
                result.add((int) temp);
                j++;
            }
        }
        return result;
    }

    /**
     * First convert the input Integer list to character list, then perform the
     * decompress algorithm, write the output into given file.
     *
     * @param outputfile filename after decompression
     * @throws IOException
     */
    public void Decompress(String outputfile, boolean verbose) throws IOException {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputfile)));
        StringBuilder sb = new StringBuilder();
        LinkedList<Integer> raw = transformFrom8to12(input);


        int j = 256;
        //enter all symbols into the table
        hashMap<Integer, String> map = new hashMap<>(10000);
        for (int i = 0; i < 256; i++) {
            map.put(i, Character.toString((char) i));
        }

        //read(priorcodeword) and output its corresponding char
        int prior = raw.remove(0);
        String priorword = map.get(prior);
        sb.append(priorword);

        while (raw.size() != 0) {
            int code = raw.remove(0);
            String codeword;

            if (!map.containsKey(code)) {
                codeword = priorword + priorword.charAt(0);
                map.put(j, priorword + priorword.charAt(0));
                sb.append(priorword + priorword.charAt(0));
                j++;
            } else {
                codeword = map.get(code);
                map.put(j, priorword + codeword.charAt(0));
                sb.append(codeword);
                j++;

            }

            if (j == MAXKEY) {
                map = new hashMap<>(10000);
                for (int i = 0; i < 256; i++) {
                    map.put(i, Character.toString((char) i));
                }
                j = 256;
            }

            priorword = codeword;

        }
        out.writeBytes(sb.toString());
        out.close();

        if (verbose) {
            System.out.println("bytes read = " + input.size() + ", bytes written = " + sb.toString().length());
        }
    }

    /**
     * Main method of LZW compression
     *
     * @param args command line input
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        String inputfile, outputfile;
        boolean verbose;
        LZWCompression compressor = new LZWCompression();

        if (args[1].equals("-v")) {
            verbose = true;
            inputfile = args[2];
            outputfile = args[3];
        } else {
            verbose = false;
            inputfile = args[1];
            outputfile = args[2];
        }

        if (args[0].equals("-c")) {
            compressor.load(inputfile);
            compressor.Compress(outputfile, verbose);

        } else if (args[0].equals("-d")) {
            compressor.load(inputfile);
            compressor.Decompress(outputfile, verbose);
        }
    }
}
