﻿using WeatherApp.Models;
using WeatherXamarim.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace WeatherXamarim
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ForecastDetailPage : ContentPage
    {
        
        public ForecastDetailPage(List info)
        {
            
            InitializeComponent();
            Title = info.Date + " Forecast";
            WeatherBackground.Source = info.weather[0].IconUrl;
            temperature.Text = info.main.temp.ToString("0.0")+"";
            humidity.Text = info.main.humidity.ToString() + "%";
            cloudiness.Text = info.clouds.all.ToString() + "%";
            wind.Text = info.wind.speed.ToString() + " m/s";
            windangle.Text = info.wind.deg.ToString() + "º";
            description.Text = Utils.FirstCharToUpper(info.weather[0].description);
            WeatherBackground.Source = Utils.GetBackgroundImage(info.weather[0].description);
            date.Text = info.Date.ToString();
            //SetBackgroundImage(root.weather[0].description);
            /*if (info.rain != null)
            {
                if (info.rain.__invalid_name__3h > 0)
                    description.Text += " (" + info.rain.__invalid_name__3h.ToString() + " mm)";
            }*/
        }
    }
}