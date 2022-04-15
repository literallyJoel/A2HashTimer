package HashSpeedTest;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.hash.Hashing;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class HashSpeedTest {

    enum Mode{
        SHA1,
        SHA256,
        SHA512,
        MD5,
        ADLER32,
        CRC32,
        CRC32C,
        FARMHASHFINGERPRINT64,
        SIPHASH24
    }
    static ArrayList<String[]> data = new ArrayList<>();

    public static void main(String[] args) {

        try {
            if(args.length == 4){

                String mode = args[0];
                int runs = Integer.parseInt(args[1]);
                calculateTimes(Mode.valueOf(mode), runs, args[2]);
                writeToCSV(args[3], data);
            }else{
                throw new Exception("Invalid length");
            }
        }catch(Exception e){
            System.out.println("Invalid Usage!\nUsage: HashText [Mode] [Number of Runs] [Phrase to hash] [Path to Output]");
            System.out.println("Currently supported modes are: SHA1, SHA256, SHA512, MD5, ADLER32, CRC32, CEC32C, FARMHASHFINGERPRINT64, SIPHASH24");
        }
    }


    public static void setupHeader(int runs){
        String[] Header = new String[runs+2];
        Header[0] = "Run #";
        Header[runs+1] = "Average (ns)";
        IntStream.range(1, runs+1).forEach(i -> Header[i] = Integer.toString(i-1));
        data.add(Header);
    }


    public static void calculateTimes(Mode mode, int runs, String phrase){
        if(data.isEmpty()){
            setupHeader(runs);
        }
        ArrayList<String> times = new ArrayList<>();
        //Adds the name of the algorithm we're using
        times.add(mode.toString());

        IntStream.range(1, runs+2).forEach(i -> times.add(Long.toString(timeHash(mode, phrase))));

        //Removes the first result, as Just In Time Compilation can make it way larger than the rest of them
        //meaning there's a good chance that it's useless data.
        times.remove(1);
        times.add(calculateAvg(times));

        //The openCSV library requires a String array, so we have to convert the array list.
        data.add(times.toArray(String[]::new));
    }

    public static String calculateAvg(ArrayList<String> data) {
        return Double.toString(IntStream.range(1, data.size() - 2).mapToLong(i -> Long.parseLong(data.get(i))).average().orElse(-1));
    }

    //Uses the OpenCSV Library to write to CSV
    public static void writeToCSV(String path, ArrayList<String[]> data) {
        File file = new File(path);
        try {
            FileWriter output = new FileWriter(file);
            CSVWriter writer = new CSVWriter(output);
            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Uses the Guava Library by Google to both perform and time the hashes
    public static long timeHash(Mode mode, String m){
        Stopwatch timer = Stopwatch.createUnstarted();
        switch(mode){
            case SHA1:
                timer.start();
                Hashing.sha1().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case SHA256:
                timer.start();
                Hashing.sha256().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case SHA512:
                timer.start();
                Hashing.sha512().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case MD5:
                timer.start();
                Hashing.md5().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case ADLER32:
                timer.start();
                Hashing.adler32().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case CRC32:
                timer.start();
                Hashing.crc32().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case CRC32C:
                timer.start();
                Hashing.crc32c().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case FARMHASHFINGERPRINT64:
                timer.start();
                Hashing.farmHashFingerprint64().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
            case SIPHASH24:
                timer.start();
                Hashing.sipHash24().hashString(m, Charsets.UTF_8).toString();
                return timer.stop().elapsed().toNanos();
        }

        //This can literally never be returned so the value doesn't matter
        //If a value that isn't in the case is used, the program fails before we reach this point.
        return 0;
    }




}

