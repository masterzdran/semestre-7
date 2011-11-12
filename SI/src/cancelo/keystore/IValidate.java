package cancelo.keystore;

import java.security.cert.Certificate;

public interface IValidate {
    public boolean isValid(Certificate certificate);
}
