/*
 * Copyright (c) 2014.  @石头哥哥
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package proto;

import java.io.Serializable;

/**
 * 记录悬赏活动 排行信息
 */
public class Roleofferinfo implements Serializable ,Comparable<Roleofferinfo>{
    private Integer id;

    private Integer roleid;

    private Integer rolecurrentfen;

    private Integer rolecurrentrank;

    private Long oneCooltime;     //冷却时间  难度系数1

    private Long twoCooltime;

    private Long threeCooltime;

    private String rolename;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public Integer getRolecurrentfen() {
        return rolecurrentfen;
    }

    public void setRolecurrentfen(Integer rolecurrentfen) {
        this.rolecurrentfen = rolecurrentfen;
    }

    public Integer getRolecurrentrank() {
        return rolecurrentrank;
    }

    public void setRolecurrentrank(Integer rolecurrentrank) {
        this.rolecurrentrank = rolecurrentrank;
    }

    public Long getOneCooltime() {
        return oneCooltime;
    }

    public void setOneCooltime(Long oneCooltime) {
        this.oneCooltime = oneCooltime;
    }

    public Long getTwoCooltime() {
        return twoCooltime;
    }

    public void setTwoCooltime(Long twoCooltime) {
        this.twoCooltime = twoCooltime;
    }

    public Long getThreeCooltime() {
        return threeCooltime;
    }

    public void setThreeCooltime(Long threeCooltime) {
        this.threeCooltime = threeCooltime;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename == null ? null : rolename.trim();
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
        Roleofferinfo other = (Roleofferinfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getRoleid() == null ? other.getRoleid() == null : this.getRoleid().equals(other.getRoleid()))
                && (this.getRolecurrentfen() == null ? other.getRolecurrentfen() == null : this.getRolecurrentfen().equals(other.getRolecurrentfen()))
                && (this.getRolecurrentrank() == null ? other.getRolecurrentrank() == null : this.getRolecurrentrank().equals(other.getRolecurrentrank()))
                && (this.getOneCooltime() == null ? other.getOneCooltime() == null : this.getOneCooltime().equals(other.getOneCooltime()))
                && (this.getTwoCooltime() == null ? other.getTwoCooltime() == null : this.getTwoCooltime().equals(other.getTwoCooltime()))
                && (this.getThreeCooltime() == null ? other.getThreeCooltime() == null : this.getThreeCooltime().equals(other.getThreeCooltime()))
                && (this.getRolename() == null ? other.getRolename() == null : this.getRolename().equals(other.getRolename()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRoleid() == null) ? 0 : getRoleid().hashCode());
        result = prime * result + ((getRolecurrentfen() == null) ? 0 : getRolecurrentfen().hashCode());
        result = prime * result + ((getRolecurrentrank() == null) ? 0 : getRolecurrentrank().hashCode());
        result = prime * result + ((getOneCooltime() == null) ? 0 : getOneCooltime().hashCode());
        result = prime * result + ((getTwoCooltime() == null) ? 0 : getTwoCooltime().hashCode());
        result = prime * result + ((getThreeCooltime() == null) ? 0 : getThreeCooltime().hashCode());
        result = prime * result + ((getRolename() == null) ? 0 : getRolename().hashCode());
        return result;
    }

    /**
     * 实现比较接口
     * @param o
     * @return
     */
    @Override
    public int compareTo(Roleofferinfo o) {
        int temp =  (this.getRolecurrentfen() - o.getRolecurrentfen());
        if (temp > 0) {
            return 1;
        } else if (temp == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}