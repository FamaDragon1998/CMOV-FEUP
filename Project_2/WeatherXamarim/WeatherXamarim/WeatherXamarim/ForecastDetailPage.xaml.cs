using System.Diagnostics;
using WeatherApp.Models;
using WeatherXamarim.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace WeatherXamarim
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ForecastDetailPage : ContentPage
    {
        private List info;

        string ForecastDate;

        public ForecastDetailPage(List info)
        {

            this.info = info;
            Debug.Write("info", info.wind.deg.ToString());
            InitializeComponent();

            /*
             * 
             *    Humidity.Text = root.main.humidity.ToString() + "%";
            Pressure.Text = root.main.pressure.ToString() + " hpa";
            Cloudiness.Text = root.clouds.all.ToString() + "%";
            Wind.Text = root.wind.speed.ToString() + " m/s";
            Date.Text = DateTime.Today.Date.ToString("dd/MM/yyyy");
             */

            Title = info.Date + " Forecast";
            Humidity.Text = info.main.humidity.ToString() + "%";
            Clouds.Text = info.clouds.all.ToString() + "%";
            Wind_Speed.Text = info.wind.speed.ToString() + "m/s";
            Wind_Deg.Text = info.wind.deg.ToString() + "º";
            Description.Text = Utils.FirstCharToUpper(info.weather[0].description);
            //SetBackgroundImage(root.weather[0].description);
            if (info.rain != null)
            {
                if (info.rain.__invalid_name__3h > 0)
                    Description.Text += " (" + info.rain.__invalid_name__3h.ToString() + " mm)";
            }

             /* if (info.rain != null)
                  if (info.rain.__invalid_name__3h > 0)
                  {
                      Content.
                  }*/
        }
    }
}