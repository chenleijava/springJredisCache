/*
 * Copyright (c) 2014.  @石头哥哥
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package springJredisCache.Serializations;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import springJredisCache.JRedisCacheException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by chenlei on 14-10-4.
 * PROJECT_NAME: springJredisCache
 * PACKAGE_NAME: springJredisCache.Serializations
 * 基于fst的序列化方案
 */
public class FstSer implements Serialization{


    /**
     * @param obj
     * @return
     */
    @Override
    public byte[] ObjSerialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FSTObjectOutput out = null;
        try {
            // stream closed in the finally
            byteArrayOutputStream = new ByteArrayOutputStream(512);
            out = new FSTObjectOutput(byteArrayOutputStream);  //32000  buffer size
            out.writeObject(obj);
            out.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                obj = null;
                if (out != null) {
                    out.close();    //call flush byte buffer
                    out = null;
                }
                if (byteArrayOutputStream != null) {

                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * @param bytes
     * @return
     */
    @Override
    public Object ObjDeserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        FSTObjectInput in = null;
        try {
            // stream closed in the finally
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            in = new FSTObjectInput(byteArrayInputStream);
            return in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new JRedisCacheException(ex);
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                bytes = null;
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                    byteArrayInputStream = null;
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
}
