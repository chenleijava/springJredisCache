package proto;

import java.io.Serializable;

public class TRoleEqu implements Serializable {

    private Integer equid = 0;

    private Integer equtypeid = 0;

    private Byte itemtype = 0;

    private Integer ownerid = 0;

    private Byte equpinzhi = 0;

    private Byte equpinjie = 0;

    private Integer equpinjieexp = 0;

    private Integer equlevel = 0;

    private Byte equtype = 0;

    private Byte equlocation = 0;

    private Long equtimeout = 0l;

    private Integer equprice = 0;

    private Boolean equshowbag = false;

    private Integer effecttype1 = 0;

    private Double effectnum1 = 0d;

    private Double effectnumgrowup1 = 0d;

    private Integer effecttype2 = 0;

    private Double effectnum2 = 0d;

    private Double effectnumgrowup2 = 0d;

    private Integer effecttype3 = 0;

    private Double effectnum3 = 0d;

    private Double effectnumgrowup3;

    private static final long serialVersionUID = 1L;

    public Integer getEquid() {
        return equid;
    }

    public void setEquid(Integer equid) {
        this.equid = equid;
    }

    public Integer getEqutypeid() {
        return equtypeid;
    }

    public void setEqutypeid(Integer equtypeid) {
        this.equtypeid = equtypeid;
    }

    public Byte getItemtype() {
        return itemtype;
    }

    public void setItemtype(Byte itemtype) {
        this.itemtype = itemtype;
    }

    public Integer getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(Integer ownerid) {
        this.ownerid = ownerid;
    }

    public Byte getEqupinzhi() {
        return equpinzhi;
    }

    public void setEqupinzhi(Byte equpinzhi) {
        this.equpinzhi = equpinzhi;
    }

    public Byte getEqupinjie() {
        return equpinjie;
    }

    public void setEqupinjie(Byte equpinjie) {
        this.equpinjie = equpinjie;
    }

    public Integer getEqupinjieexp() {
        return equpinjieexp;
    }

    public void setEqupinjieexp(Integer equpinjieexp) {
        this.equpinjieexp = equpinjieexp;
    }

    public Integer getEqulevel() {
        return equlevel;
    }

    public void setEqulevel(Integer equlevel) {
        this.equlevel = equlevel;
    }

    public Byte getEqutype() {
        return equtype;
    }

    public void setEqutype(Byte equtype) {
        this.equtype = equtype;
    }

    public Byte getEqulocation() {
        return equlocation;
    }

    public void setEqulocation(Byte equlocation) {
        this.equlocation = equlocation;
    }

    public Long getEqutimeout() {
        return equtimeout;
    }

    public void setEqutimeout(Long equtimeout) {
        this.equtimeout = equtimeout;
    }

    public Integer getEquprice() {
        return equprice;
    }

    public void setEquprice(Integer equprice) {
        this.equprice = equprice;
    }

    public Boolean getEqushowbag() {
        return equshowbag;
    }

    public void setEqushowbag(Boolean equshowbag) {
        this.equshowbag = equshowbag;
    }

    public Integer getEffecttype1() {
        return effecttype1;
    }

    public void setEffecttype1(Integer effecttype1) {
        this.effecttype1 = effecttype1;
    }

    public Double getEffectnum1() {
        return effectnum1;
    }

    public void setEffectnum1(Double effectnum1) {
        this.effectnum1 = effectnum1;
    }

    public Double getEffectnumgrowup1() {
        return effectnumgrowup1;
    }

    public void setEffectnumgrowup1(Double effectnumgrowup1) {
        this.effectnumgrowup1 = effectnumgrowup1;
    }

    public Integer getEffecttype2() {
        return effecttype2;
    }

    public void setEffecttype2(Integer effecttype2) {
        this.effecttype2 = effecttype2;
    }

    public Double getEffectnum2() {
        return effectnum2;
    }

    public void setEffectnum2(Double effectnum2) {
        this.effectnum2 = effectnum2;
    }

    public Double getEffectnumgrowup2() {
        return effectnumgrowup2;
    }

    public void setEffectnumgrowup2(Double effectnumgrowup2) {
        this.effectnumgrowup2 = effectnumgrowup2;
    }

    public Integer getEffecttype3() {
        return effecttype3;
    }

    public void setEffecttype3(Integer effecttype3) {
        this.effecttype3 = effecttype3;
    }

    public Double getEffectnum3() {
        return effectnum3;
    }

    public void setEffectnum3(Double effectnum3) {
        this.effectnum3 = effectnum3;
    }

    public Double getEffectnumgrowup3() {
        return effectnumgrowup3;
    }

    public void setEffectnumgrowup3(Double effectnumgrowup3) {
        this.effectnumgrowup3 = effectnumgrowup3;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TRoleEqu other = (TRoleEqu) that;
        return (this.getEquid() == null ? other.getEquid() == null : this.getEquid().equals(other.getEquid()))
                && (this.getEqutypeid() == null ? other.getEqutypeid() == null : this.getEqutypeid().equals(other.getEqutypeid()))
                && (this.getItemtype() == null ? other.getItemtype() == null : this.getItemtype().equals(other.getItemtype()))
                && (this.getOwnerid() == null ? other.getOwnerid() == null : this.getOwnerid().equals(other.getOwnerid()))
                && (this.getEqupinzhi() == null ? other.getEqupinzhi() == null : this.getEqupinzhi().equals(other.getEqupinzhi()))
                && (this.getEqupinjie() == null ? other.getEqupinjie() == null : this.getEqupinjie().equals(other.getEqupinjie()))
                && (this.getEqupinjieexp() == null ? other.getEqupinjieexp() == null : this.getEqupinjieexp().equals(other.getEqupinjieexp()))
                && (this.getEqulevel() == null ? other.getEqulevel() == null : this.getEqulevel().equals(other.getEqulevel()))
                && (this.getEqutype() == null ? other.getEqutype() == null : this.getEqutype().equals(other.getEqutype()))
                && (this.getEqulocation() == null ? other.getEqulocation() == null : this.getEqulocation().equals(other.getEqulocation()))
                && (this.getEqutimeout() == null ? other.getEqutimeout() == null : this.getEqutimeout().equals(other.getEqutimeout()))
                && (this.getEquprice() == null ? other.getEquprice() == null : this.getEquprice().equals(other.getEquprice()))
                && (this.getEqushowbag() == null ? other.getEqushowbag() == null : this.getEqushowbag().equals(other.getEqushowbag()))
                && (this.getEffecttype1() == null ? other.getEffecttype1() == null : this.getEffecttype1().equals(other.getEffecttype1()))
                && (this.getEffectnum1() == null ? other.getEffectnum1() == null : this.getEffectnum1().equals(other.getEffectnum1()))
                && (this.getEffectnumgrowup1() == null ? other.getEffectnumgrowup1() == null : this.getEffectnumgrowup1().equals(other.getEffectnumgrowup1()))
                && (this.getEffecttype2() == null ? other.getEffecttype2() == null : this.getEffecttype2().equals(other.getEffecttype2()))
                && (this.getEffectnum2() == null ? other.getEffectnum2() == null : this.getEffectnum2().equals(other.getEffectnum2()))
                && (this.getEffectnumgrowup2() == null ? other.getEffectnumgrowup2() == null : this.getEffectnumgrowup2().equals(other.getEffectnumgrowup2()))
                && (this.getEffecttype3() == null ? other.getEffecttype3() == null : this.getEffecttype3().equals(other.getEffecttype3()))
                && (this.getEffectnum3() == null ? other.getEffectnum3() == null : this.getEffectnum3().equals(other.getEffectnum3()))
                && (this.getEffectnumgrowup3() == null ? other.getEffectnumgrowup3() == null : this.getEffectnumgrowup3().equals(other.getEffectnumgrowup3()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEquid() == null) ? 0 : getEquid().hashCode());
        result = prime * result + ((getEqutypeid() == null) ? 0 : getEqutypeid().hashCode());
        result = prime * result + ((getItemtype() == null) ? 0 : getItemtype().hashCode());
        result = prime * result + ((getOwnerid() == null) ? 0 : getOwnerid().hashCode());
        result = prime * result + ((getEqupinzhi() == null) ? 0 : getEqupinzhi().hashCode());
        result = prime * result + ((getEqupinjie() == null) ? 0 : getEqupinjie().hashCode());
        result = prime * result + ((getEqupinjieexp() == null) ? 0 : getEqupinjieexp().hashCode());
        result = prime * result + ((getEqulevel() == null) ? 0 : getEqulevel().hashCode());
        result = prime * result + ((getEqutype() == null) ? 0 : getEqutype().hashCode());
        result = prime * result + ((getEqulocation() == null) ? 0 : getEqulocation().hashCode());
        result = prime * result + ((getEqutimeout() == null) ? 0 : getEqutimeout().hashCode());
        result = prime * result + ((getEquprice() == null) ? 0 : getEquprice().hashCode());
        result = prime * result + ((getEqushowbag() == null) ? 0 : getEqushowbag().hashCode());
        result = prime * result + ((getEffecttype1() == null) ? 0 : getEffecttype1().hashCode());
        result = prime * result + ((getEffectnum1() == null) ? 0 : getEffectnum1().hashCode());
        result = prime * result + ((getEffectnumgrowup1() == null) ? 0 : getEffectnumgrowup1().hashCode());
        result = prime * result + ((getEffecttype2() == null) ? 0 : getEffecttype2().hashCode());
        result = prime * result + ((getEffectnum2() == null) ? 0 : getEffectnum2().hashCode());
        result = prime * result + ((getEffectnumgrowup2() == null) ? 0 : getEffectnumgrowup2().hashCode());
        result = prime * result + ((getEffecttype3() == null) ? 0 : getEffecttype3().hashCode());
        result = prime * result + ((getEffectnum3() == null) ? 0 : getEffectnum3().hashCode());
        result = prime * result + ((getEffectnumgrowup3() == null) ? 0 : getEffectnumgrowup3().hashCode());
        return result;
    }
}