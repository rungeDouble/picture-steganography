/**
 * 这是APP的MainActivity，当然，前面还有启动界面
 */

package com.example.runge.PictureSteganography;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import filesmanage.Files_Manage_Activity;
import filestrans.Files_Trans_Activity;
import offlinefiles.Offline_Files_Choose_Activity;

import static com.example.runge.PictureSteganography.MainActivity.mMyFileList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public boolean UdpReceiveOut = true;//8秒后跳出udp接收线程
    /**
     * rungeRootFolder此程序自己的文件目录
     */
    static List<myFile> mMyFileList;
    public static MyOpenHelper mOpenHelper;
    public static SQLiteDatabase db;
    MyAdapter myAdapter;
    ListView mHistoryListview;
    String rungeRootFolder = "/sdcard/runge/";
    /**
     * 发送离线文件的按钮
     */
    private Button btnSend_offlinefiles;
    /**
     * 弹出对话框下载离线文件的按钮
     **/
    private Button btnDown_offlinefiles;
    /**
     * 点两次返回按键退出程序的时间
     */
    private long mExitTime;
    private final int requestCode = 0x101;
    /**
     * 显示离线文件传输的日志提醒的Textview，默认情况下文本为空
     */
    public static TextView offline_trans_log;
    /**
     * 在MainActivity声明两个Fab按钮，类FloatingActionButton是引入自开源库library
     */
    private com.getbase.floatingactionbutton.FloatingActionButton fab_Encode;
    private com.getbase.floatingactionbutton.FloatingActionButton fab_Decode;
    private boolean wifiFlag = true;//扫描wifi的子线程的标志位，如果已经连接上正确的wifi热点，线程将结束
    private String address;
    private ArrayList<String> arraylist = new ArrayList<String>();
    private boolean update_wifi_flag = true;
    String ip;
    private ListView listView;
    public static final int DEFAULT_PORT = 43708;
    private static final int MAX_DATA_PACKET_LENGTH = 40;
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    public boolean run = false;//判断是否接收到TCP返回，若接收到则不再继续接受
    public boolean show = false;//判断是否是由于超时而退出线程，若是则显示dialog
    private static boolean tcpout = false;
    private boolean a = false;
    private com.getbase.floatingactionbutton.FloatingActionsMenu multiple_actions;
    //开启wifi ... ...
    private WifiManager wifiManager = null;
    /**********************************************************************************************/
    private ImageView iv_scanning;
    private android.support.v4.widget.DrawerLayout rl_root;
    /*********************UdpReceive线程**********************/

    Socket socket = null;
    static DatagramSocket udpSocket = null;
    static DatagramPacket udpPacket = null;
    private boolean udpout = false;
    /*******************************************************/
    //用以存储传送到文件发送界面的IP，即接收方的IP
    public static String IP_DuiFangde;

    /*********************************rungeDB**************************************/
    public static String Device_ID = "";

    /**
     * Android6.0 获取更改系统设置的权限，app用了其他的方式，这段代码没有用到，删除也可以的
     */
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 判断是否有WRITE_SETTINGS权限
            if (!Settings.System.canWrite(this)) {
                // 申请WRITE_SETTINGS权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }
    }


    /**
     * 获取连接到手机热点设备的IP
     */
    StringBuilder resultList;
    ArrayList<String> connectedIP;

    public String getConnectDeviceIP() {

        try {
            connectedIP = getConnectIp();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        resultList = new StringBuilder();
        for (String ip : connectedIP) {
            resultList.append(ip);
            resultList.append("\n");
            try {
                connectedIP = getConnectIp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String textString = resultList.toString();
        return textString;

    }

    //从系统/proc/net/arp文件中读取出已连接的设备的信息
    //获取连接设备的IP
    private ArrayList<String> getConnectIp() throws Exception {
        ArrayList<String> connectIpList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] splitted = line.split(" +");
            if (splitted != null && splitted.length >= 4) {
                String ip = splitted[0];
                connectIpList.add(ip);
            }
        }
        return connectIpList;
    }

    /**
     * 获取系统当前的时间
     */
    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

    /**
     * 获取系统当前的时间
     */
    public static String getCurrentTime2() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

    /**
     * 获取设备唯一的标志码
     */
    public String getDeviceID() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    public String getAndroidVersion() {
        String AndroidVersion = android.os.Build.VERSION.RELEASE;
        return AndroidVersion;
    }

    public String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取安卓手机RAM
     */
    public String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获取屏幕分辨率
     **/
    public String getScreenResolution() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        String strOpt = dm.widthPixels + " * " + dm.heightPixels;
        return strOpt;
    }





    /***********************************************************************
     * app数据存储的核心代码
     **************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHistoryListview = (ListView) findViewById(R.id.tv_historylistview);
        mOpenHelper = new MyOpenHelper(this);
        //db = MyOpenHelper.getWritableDatabase();
        mMyFileList = new ArrayList<myFile>();
        // 创建MyOpenHelper实例
        mOpenHelper = new MyOpenHelper(this);
        // 得到数据库
        db = mOpenHelper.getWritableDatabase();
        // 查询数据
        Query();
        // 创建MyAdapter实例
        myAdapter = new MyAdapter(this);
        mHistoryListview.setAdapter(myAdapter);
        mHistoryListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //ListView listView = (ListView) parent;
                String filename = mMyFileList.get(position).getFilename();
                String path = mMyFileList.get(position).getPath();
                String time = mMyFileList.get(position).gettime();
                Toast.makeText(MainActivity.this, filename + " , " + path + " , " + time + position, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SelectWorkActivity.class);
                intent.putExtra("FileName", filename);
                intent.putExtra("FilePath", path);
                intent.putExtra("Time", time);

                startActivityForResult(intent, requestCode);
            }
        });

        Device_ID = getDeviceID();

        //设备之间连接的两个fab的定义以及初始化
        fab_Encode = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_encode);
        fab_Decode = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_decode);

        multiple_actions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        rl_root = (DrawerLayout) findViewById(R.id.drawer_layout);

        /**获取设备IP*/
        address = getLocalIPAddress();
        ip = address;
        fab_Decode.setOnClickListener(listener);
        fab_Encode.setOnClickListener(listener);


        /*******************************************/
        offline_trans_log = (TextView) findViewById(R.id.offline_trans_log);
        createMkdir(rungeRootFolder);

        Button cleanButton = (Button)findViewById(R.id.toClean_button);
        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 17-5-23 clean the Dadabase
                db.delete("history", null, null);
                //db.execSQL("delete from table");
                // update data in our adapter
                mMyFileList.clear();
                Query();
                myAdapter.notifyDataSetChanged();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**谷歌自带Fab的设置，将其注释掉是因为我们使用的是第三方的库*/
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        /*****************************************/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    /**************************************************************
     * 两个按钮的监听事件
     ***************************************************************/
    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v == fab_Decode) {
                // TODO: 17-5-19  悬浮decode按钮点击事件
                Intent intent = new Intent(getApplicationContext(), SelectPicActivity.class);// 启动文件管理
                intent.putExtra("title", "选择要提取信息的图片");
                intent.putExtra("Type", "decode");
                startActivityForResult(intent, 0);
                //fab_Decode.setEnabled(false);
                //fab_Encode.setEnabled(false);
            } else {
                // TODO: 17-5-19  encode悬浮按钮点击事件
                Intent intent = new Intent(getApplicationContext(), SelectPicActivity.class);// 启动文件管理
                intent.putExtra("title", "选择要加密信息的图片");
                intent.putExtra("Type", "encode");
                startActivityForResult(intent, 0);
                //点击链接的按钮之后隐藏Fab.
                //fab_Decode.setEnabled(false);
                //fab_Encode.setEnabled(false);
            }

        }
    };

    /**
     * 获取本机ip方法
     */
    private String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getLocalIpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 判断一个字符串是否是标准的IPv4地址
     */
    public static boolean isIp(String IP) {
        boolean b = false;
        //去掉IP字符串前后所有的空格
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }

        //IP = this.deleteSpace(IP);
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String s[] = IP.split("\\.");
            if (Integer.parseInt(s[0]) < 255)
                if (Integer.parseInt(s[1]) < 255)
                    if (Integer.parseInt(s[2]) < 255)
                        if (Integer.parseInt(s[3]) < 255)
                            b = true;
        }
        return b;
    }

    /**
     * 去除字符串前后的空格
     */
    public String deleteSpace(String IP) {//去掉IP字符串前后所有的空格
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        return IP;
    }

    //存储传输文件的名字以及后缀名
    public static String Trans_File_Name = "";
    public static String Trans_File_Type = "";
    public static int Trans_File_Size;
    /**
     * 重写onActivityResult()方法，获取选取要上传文件的文件路径
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0){
                //final String time = data.getStringExtra("time");
                final String fileName = data.getStringExtra("FileName");
                final String path = data.getStringExtra("FilePath");

                Trans_File_Name = fileName;
                if (!(Query(path, getCurrentTime2()))){
                    Insert(fileName, path);
                }

                Query();
                myAdapter = new MyAdapter(this);
                // 向listview中添加Adapter
                mHistoryListview.setAdapter(myAdapter);

            }else{

                //if (requestCode == this.requestCode) {
                String type;
                type = data.getStringExtra(SelectPicActivity.PATH);
                if (type == null || type.equals("")) {
                    Toast.makeText(this, R.string.no_choosed_pic, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "pic = " + type, Toast.LENGTH_SHORT).show();
                if (type.equals(SelectPicActivity.SELECT_PIC_take_photo)){
                    Intent Image_encoding = new Intent(MainActivity.this, Files_Trans_Activity.class);
                    startActivity(Image_encoding);

                }else if (type.equals(SelectPicActivity.SELECT_PIC_phone_pic)){
                    Intent Image_decoding = new Intent(MainActivity.this, Files_Trans_Activity.class);
                    startActivity(Image_decoding);

                }else if (type.equals("intenet")) {
                    //将选择的文件的名字以及路径存储下来
                /*final String fileName = data.getStringExtra("FileName");
                final String path = data.getStringExtra("FilePath");
                System.out.println("0000000000000000000000000000000000000000000000000000000000000000" + path);
                System.out.println("0000000000000000000000000000000000000000000000000000000000000000" + fileName);
                //String uploadUrl = "http://192.168.1.147/OfflineTrans/AndroidUploadAction.php";
                String uploadUrl = "http://115.28.101.196/AndroidUploadAction.php";
                new HttpThread_UpLoad(uploadUrl, path).start();//启动文件上传的线程*/
                } else if (type.equals("bluetooth")) {
                    String path = data.getStringExtra("filePath");
                    File file = new File(path);

                    Uri uri = Uri.fromFile(file);

                    //打开系统蓝牙模块并发送文件
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("*/*");
                    sharingIntent.setPackage("com.android.bluetooth");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(sharingIntent, "Share"));

                    Log.d("MainActivity", uri.getPath());//log打印返回的文件路径
                }
            }
            //}
        }
        //选择了文件发送
        if (resultCode == RESULT_OK) {
            String type = data.getStringExtra("Type");
        }
    }

    /**
     * 创建文件夹的方法createMkdir()
     */
    public static void createMkdir(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }


    /**
     * 点击两次退出程序
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {

                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bluetooth) {
            /**用系统的蓝牙模块来发送文件*/
            Intent intent = new Intent(getApplicationContext(), Offline_Files_Choose_Activity.class);
            intent.putExtra("Type", "bluetooth");
            startActivityForResult(intent, 0);

        } else if (id == R.id.nav_share) {
            //弹出对话框，进行文件提取码的输入，然后下载文件
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("扫码联系我：");
            //通过LayoutInflater来加载一个xml的布局文件作为一个View对象
            final View Dialogview = LayoutInflater.from(MainActivity.this).inflate(R.layout.share_app_dialog, null);
            //设置我们自己定义的布局文件作为弹出框的Content
            builder.setView(Dialogview);

            //设置点击确定后的事件，什么也不执行，关闭dialog
            builder.setNegativeButton("确定", null);
            builder.show();


        } else {
            if (id == R.id.nav_tranFile) {
                Intent Image_encoding = new Intent(MainActivity.this, Files_Trans_Activity.class);
                startActivity(Image_encoding);
            } else if (id == R.id.nav_filesmanage) {
                /**菜单中文件管理选项，跳转到文件管理的Activity进行文件管理的操作*/
                Intent intent = new Intent(MainActivity.this, Files_Manage_Activity.class);
                startActivity(intent);

            } else if (id == R.id.nav_Imagebank) {
                /**菜单中文件管理选项，跳转到nav_feedback操作*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        browseGallery(0);
                    } else {
                        if(shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            Toast.makeText(this,"External storage permission is needed to explore gallery.", Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                    }
                }
                else {
                    browseGallery(0);
                }
            }


            else if (id == R.id.nav_softversion) {
                /**菜单中“版本”选项的弹出显示版本信息的对话框*/
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("软件版本")
                        .setMessage("版本号：" + BuildConfig.VERSION_CODE + "\n版本名：" + BuildConfig.VERSION_NAME)
                        .setPositiveButton("确定", null)
                        .show();
                return true;

            } else if (id == R.id.nav_softdescribe) {
                /**菜单中“软件描述”选项的弹出对话框*/
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("软件描述")
                        .setMessage("此APP利用隐写术隐写信息到图片。\n")
                        .setPositiveButton("确定", null)
                        .show();
                return true;

            } else if (id == R.id.nav_aboutus) {
                /**菜单中“关于我们”选项的弹出对话框*/
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("关于我")
                        .setMessage("本程序由\n吴睿(wr15169@gmail.com)\n开发!\n指导老师：肖凌")
                        .setPositiveButton("确定", null)
                        .show();
                return true;

            } else if (id == R.id.nav_androidversion) {
                /**菜单中“安卓版本”选项的弹出对话框*/
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("本机的安卓版本")
                        .setMessage("Android SDK:" + Build.VERSION.SDK + "\nAndroid 版本号:" + Build.VERSION.RELEASE)
                        .setPositiveButton("确定", null)
                        .show();
                return true;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**************************************************************
     * 系统图库模块
     * @param requestCode
     */
    private void browseGallery(int requestCode){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void finish() {
        super.finish();
        db.close();
        android.os.Process.killProcess(android.os.Process.myPid()); /**杀死这个应用的全部进程*/

    }

    // 创建MyAdapter继承BaseAdapter
    class MyAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mMyFileList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 从personList取出Person
            myFile p = mMyFileList.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_list, null);
                viewHolder.imageshow = (ImageView) convertView
                        .findViewById(R.id.tv_hitoryImageview);
                viewHolder.txt_filename = (TextView) convertView
                        .findViewById(R.id.tv_filename);
                viewHolder.txt_path = (TextView) convertView
                        .findViewById(R.id.tv_filepath);
                viewHolder.txt_time = (TextView) convertView
                        .findViewById(R.id.tv_useTime);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //向TextView中插入数据
            Bitmap mpic = BitmapFactory.decodeFile(p.getPath());
            viewHolder.imageshow.setImageBitmap(mpic);
            viewHolder.txt_filename.setText(p.getFilename());
            viewHolder.txt_path.setText(p.getPath());
            viewHolder.txt_time.setText(p.gettime());

            viewHolder.imageshow.setVisibility(View.INVISIBLE);
            viewHolder.txt_filename.setVisibility(View.INVISIBLE);
            viewHolder.txt_path.setVisibility(View.INVISIBLE);


            return convertView;
        }
    }

    class ViewHolder {
        private ImageView imageshow;
        private TextView txt_filename;
        private TextView txt_path;
        private TextView txt_time;
    }

    // 插入数据
    public void Insert(String a, String b) {
        ContentValues values = new ContentValues();
        values.put("filename", a);
        values.put("path", b);
        values.put("time", getCurrentTime2());
        db.insert("history", null, values);
    }

    public static void insertfromEncode() {
        if (!(Query(EncodingActivity.Image_Path, EncodingActivity.Image_time))) {
            ContentValues values = new ContentValues();
            values.put("filename", EncodingActivity.Image_Name);
            values.put("path", EncodingActivity.Image_Path);
            values.put("time", EncodingActivity.Image_time);
            MainActivity.db.insert("history", null, values);
        }
    }

    public static void insertfromDecode() {
        if (!(Query(EncodingActivity.Image_Path, DecodingActivity.Image_Time))) {
            ContentValues values = new ContentValues();
            values.put("filename", DecodingActivity.Image_Name);
            values.put("path", DecodingActivity.Image_Path);
            values.put("time", DecodingActivity.Image_Time);
            MainActivity.db.insert("history", null, values);
        }
    }

    // 查询数据
    public void Query() {
        Cursor cursor = db.query("history", null, null, null, null, null, null);
        for(int i=0;i<cursor.getCount();i++)
        {
            if(cursor.moveToFirst())                                     //判断游标是否为空
            {
                cursor.move(i);                                      //移动到指定记录
                String _id = cursor.getString(0);
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                String time = cursor.getString(3);
                myFile mfile = new myFile(_id, name, path, time);
                mMyFileList.add(mfile);
            }
            else{
                System.out.println("no record");
                break;
            }

        }

        cursor.close();
        return;
    }

    public static boolean Query(String mpath, String mtime) {
        Cursor cursor = db.query("history", null, null, null, null, null, null);
        for(int i=0;i<cursor.getCount();i++)
        {
            if(cursor.moveToFirst())                                     //判断游标是否为空
            {
                cursor.move(i);                                      //移动到指定记录
                String filename = cursor.getString(1);
                String path = cursor.getString(2);
                String time = cursor.getString(3);
                if (mStringequals(path, mpath) && mStringequals(time, mtime)) {
                    return true;
                }
            }
            else{
                System.out.println("no record");
                break;
            }
        }
        return false;
    }

    public static boolean mStringequals(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a != null && b != null) {
            if (a.equals(b)) {
                return true;
            }
        }
        return false;
    }

}


class myFile {

    private String _id;
    private String filename;
    private String time;
    private String path;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String name) {
        this.filename = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String gettime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return filename + ", " + path + ", " + time;
    }

    public myFile(String _id, String filename, String path, String time) {
        super();
        this._id = _id;
        this.filename = filename;
        this.path = path;
        this.time = time;
    }

    // 创建MyAdapter继承BaseAdapter
    class MyAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mMyFileList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        public List<myFile> getData() {
            return mMyFileList;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 从personList取出Person
            myFile p = mMyFileList.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_list, null);
                viewHolder.imageshow = (ImageView) convertView
                        .findViewById(R.id.tv_hitoryImageview);
                viewHolder.txt_filename = (TextView) convertView
                        .findViewById(R.id.tv_filename);
                viewHolder.txt_path = (TextView) convertView
                        .findViewById(R.id.tv_filepath);
                viewHolder.txt_time = (TextView) convertView
                        .findViewById(R.id.tv_useTime);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //向TextView中插入数据
            Bitmap mpic = BitmapFactory.decodeFile(p.getPath());
            viewHolder.imageshow.setImageBitmap(mpic);
            viewHolder.txt_filename.setText(p.getFilename());
            viewHolder.txt_path.setText(p.getPath());
            viewHolder.txt_time.setText(p.gettime());

            viewHolder.imageshow.setVisibility(View.INVISIBLE);
            viewHolder.txt_filename.setVisibility(View.INVISIBLE);
            viewHolder.txt_path.setVisibility(View.INVISIBLE);


            return convertView;
        }

        class ViewHolder {
            private ImageView imageshow;
            private TextView txt_filename;
            private TextView txt_path;
            private TextView txt_time;
        }
    }

}

class MyOpenHelper extends SQLiteOpenHelper {

    public MyOpenHelper(Context context) {
        //创建数据库
        super(context, "picturestegranography2333.db", null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //创建表
        db.execSQL("create table history(_id integer primary key autoincrement, filename char(10), path integer(20), time char(20) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

}

