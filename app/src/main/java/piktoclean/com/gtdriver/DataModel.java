package piktoclean.com.gtdriver;

import android.net.Uri;

public class DataModel {

    String name;
    String number;
    String address;
    Uri uri;
    public DataModel(String name, String number, String address, Uri uri ) {
        this.name=name;
        this.number=number;
        this.address=address;
        this.uri=uri;
    }

    public String getName() {
        return name;
    }


    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public Uri getPic() {
        return uri;
    }

}
