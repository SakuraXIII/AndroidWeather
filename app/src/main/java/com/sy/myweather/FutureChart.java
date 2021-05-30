package com.sy.myweather;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sy.myweather.data.FutureWeather;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class FutureChart extends AppCompatActivity {

    private List<PointValue> mPointValuesLow = new ArrayList<>();
    private List<PointValue> mPointValuesHigh = new ArrayList<>();
    private List<AxisValue> mAxisXValues = new ArrayList<>();
    private LineChartView lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_chart);
        lineChart = (LineChartView) findViewById(R.id.line_chart);
        init();
        initLineChart();//初始化
        findViewById(R.id.back).setOnClickListener(view -> finish());
    }

    private void init() {
        ArrayList<FutureWeather> weather = (ArrayList<FutureWeather>) getIntent().getSerializableExtra("weather");
        for (int i = 0; i < weather.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(weather.get(i).getDate().split("-",2)[1]));//获取x轴的标注
            String temperature = weather.get(i).getTemperature();
            String[] split = temperature.substring(0, temperature.length() - 1).trim().split("/");
            mPointValuesLow.add(new PointValue(i, Float.parseFloat(split[0])));//获取坐标点
            mPointValuesHigh.add(new PointValue(i, Float.parseFloat(split[1])));//获取坐标点
        }
    }


    private void initLineChart() {
        Line low = new Line(mPointValuesLow).setColor(Color.parseColor("#5698c3"));  //折线的颜色（橙色）
        Line high = new Line(mPointValuesHigh).setColor(Color.parseColor("#ff9900"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        lines.add(low);
        lines.add(high);
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            lines.get(i).setCubic(false);//曲线是否平滑，即是曲线还是折线
            lines.get(i).setFilled(false);//是否填充曲线的面积
            lines.get(i).setHasLabels(true);//曲线的数据坐标是否加上备注
//            lines.get(i).setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            lines.get(i).setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            lines.get(i).setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        }
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setName("");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);
    }


}