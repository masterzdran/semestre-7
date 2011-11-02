package pt.isel.deetc.leic.si.keystore;

import java.security.cert.Certificate;

public interface IKeyStore {
	
	public boolean isValid(Certificate certificate);

}
