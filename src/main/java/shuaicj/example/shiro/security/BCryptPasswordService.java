package shuaicj.example.shiro.security;

import org.apache.shiro.authc.credential.PasswordService;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Support bcrypt.
 *
 * See https://github.com/soluvas/soluvas-framework/blob/master/security/src/main/java/org/soluvas/security/shiro/BCryptPasswordService.java
 *
 * @author shuaicj 2018/07/18
 */
public class BCryptPasswordService implements PasswordService {

    @Override
    public String encryptPassword(Object plaintextPassword) throws IllegalArgumentException {
        String str;
        if (plaintextPassword instanceof String) {
            str = (String) plaintextPassword;
        } else if (plaintextPassword instanceof char[]) {
            str = new String((char[]) plaintextPassword);
        } else if (plaintextPassword instanceof byte[]) {
            str = new String((byte[]) plaintextPassword);
        } else {
            throw new IllegalArgumentException("Unsupported password type: " + plaintextPassword.getClass().getName());
        }
        return BCrypt.hashpw(str, BCrypt.gensalt());
    }

    @Override
    public boolean passwordsMatch(Object submittedPlaintext, String encrypted) {
        return BCrypt.checkpw(new String((char[]) submittedPlaintext), encrypted);
    }
}
