package lk.jiat.shapeway.models;

public class PlanItem {

    public String title, subtitle;
   public int icon, progress;

    public PlanItem(String title, String subtitle, int icon, int progress) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.progress = progress;
    }
}
