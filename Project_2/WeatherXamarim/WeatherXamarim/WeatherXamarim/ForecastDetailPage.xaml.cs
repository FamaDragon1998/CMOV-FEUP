using System.Diagnostics;
using WeatherApp.Models;
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

            ForecastDate = info.Date + " Forecast";
             /* if (info.rain != null)
                  if (info.rain.__invalid_name__3h > 0)
                  {
                      Content.
                  }*/
        }
    }
}