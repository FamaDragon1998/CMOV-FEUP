using System;
using System.Collections.Generic;
using WeatherApp.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Entry = Microcharts.Entry;
using SkiaSharp;
using Microcharts;

namespace WeatherXamarim
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ForecastChart : ContentPage
    {
        public ForecastChart(RootObjectForecast root)
        {
            InitializeComponent();
            Title = "Forecast Chart";

            List<Entry> entries = new List<Entry>();

            foreach (var element in root.list)
            {

                float temperature = (float)element.main.temp;

                var entry = new Entry(temperature)
                {
                    Color = SKColor.Parse("#FF1493"),
                    Label = element.Date,
                    ValueLabel = temperature.ToString() + " ºC"
                };
                entries.Add(entry);
            }

            ForecastChartView.Chart = new LineChart
            {
                Entries = entries,
                LineMode = LineMode.Straight,
                LabelTextSize = 30f,
            };
        }
    }
}