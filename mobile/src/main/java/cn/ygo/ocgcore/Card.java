package cn.ygo.ocgcore;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

import cn.ygo.ocgcore.enums.CardType;

public class Card extends CardData implements Parcelable {
    public static final int SETCODE_MAX = 4;
    public String Name;
    public String Desc;
    public String[] Strs;

    public Card() {
        Strs = new String[0x10];
    }

    public Card(CardData cardData) {
        super();
        if (cardData != null) {
            this.Code = cardData.Code;
            this.Alias = cardData.Alias;
            this.Setcode = cardData.Setcode;
            this.Type = cardData.Type;
            this.Level = cardData.Level;
            this.Attribute = cardData.Attribute;
            this.Race = cardData.Race;
            this.Attack = cardData.Attack;
            this.Defense = cardData.Defense;
            this.LScale = cardData.LScale;
            this.RScale = cardData.RScale;
            this.dbFile = cardData.dbFile;
            this.Category = cardData.Category;
        }
    }

    public boolean isType(CardType type) {
        return ((Type & type.value()) != 0);
    }

    public boolean isExtraCard() {
        return (isType(CardType.Fusion) || isType(CardType.Synchro) || isType(CardType.Xyz));
    }

    public long[] getSetCode() {
        long[] setcodes = new long[SETCODE_MAX];
        for (int i = 0, k = 0; i < SETCODE_MAX; k += 0x10, i++) {
            setcodes[i] = (Setcode >> k) & 0xffff;
        }
        return setcodes;
    }

    public void setSetCode(long[] setcodes) {
        int i = 0;
        this.Setcode = 0;
        if (setcodes != null) {
            for (long sc : setcodes) {
                this.Setcode += (sc << i);
                i += 0x10;
            }
        }
    }

    @Override
    public String toString() {
        return "Card{" +
                "Code=" + Code +
                ", Alias=" + Alias +
                ", Setcode=" + Setcode +
                ", Type=" + Type +
                ", Level=" + Level +
                ", Attribute=" + Attribute +
                ", Race=" + Race +
                ", Attack=" + Attack +
                ", Defense=" + Defense +
                ", LScale=" + LScale +
                ", RScale=" + RScale +
                ", Name='" + Name + '\'' +
                ", Desc='" + Desc + '\'' +
                ", Strs=" + Arrays.toString(Strs) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.Name);
        dest.writeString(this.Desc);
        dest.writeStringArray(this.Strs);
    }

    protected Card(Parcel in) {
        super(in);
        this.Name = in.readString();
        this.Desc = in.readString();
        this.Strs = in.createStringArray();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}