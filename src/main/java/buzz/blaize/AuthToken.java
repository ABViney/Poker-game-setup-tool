package buzz.blaize;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AuthToken {
    /// 8 keys, for a max of 8 players (bots) in one game
    private static List<Pair<Boolean, AuthToken>> botKeys = new ArrayList<>(8);
    static {
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("103", "naje", "bahms.58LQaSByKuNJhj7B.YesZCbNTG7mVgp44DTFe4I2on2UZyIguN49VBi81CC8"))); // 103, naje
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("104", "ear", "bahms.pGuH2qRnQ--NwLNV.8qmQJk8sek27G-_6-VZ6I_OZ2SEgqI-CGo_kPCSWONk"))); // 104, ear
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("105", "flow", "bahms.DkNHlglZi0ltjGAV.JDOYIRg2yT-0Y96YwN3bDtOh-Ds_eG9RRCsvWiMXSIs"))); // 105, flow
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("110", "der", "bahms.rI-1Wjw1PQPWyqd2.EDcLkXXyHCWtLpMy2hqisxlk5Fwr9_IwsuPzKqZ3S88"))); // 110, der
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("106", "jane", "bahms.RHGz6ETXDWxKdO_R.pIKmg--V7NX-BnoPpuT71pi0NzFw6MgtpIX2rWzv_nQ"))); // 106, jane
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("107", "red", "bahms.Dq0akyrdSJx8a1Jx.MexJPy42D_65zjzgnTuPO2AvEABwKyKGa80S-ZCz4eY"))); // 107, red
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("108", "rae", "bahms.aeGwNz4gn_NBk-gX.j2EWqnPA29s1SZ5LlolhJnZ89ZTsl_ufznNXYfJfvuc"))); // 108, rae
        botKeys.add(new Pair<>(new Boolean(false), new AuthToken("109", "wolf", "bahms.ij66RZCqhEa5VsSF.741lwcgeYPnEFgmcCXdS6En7ibB0lMM1o1nCWyJmfIY"))); // 109, wolf
    }
    @Nullable
    public static AuthToken allocateAuthToken() {
        for (Pair<Boolean, AuthToken> botKey : botKeys) {
            if (botKey.getKey().booleanValue() == false) {
                botKey.setKey(new Boolean(true));
                return botKey.getValue();
            }
        }
        return null;
    }
    public static void releaseAuthToken(AuthToken token) {
        for (Pair<Boolean, AuthToken> botKey : botKeys) {
            if (botKey.getValue().equals(token)) {
                botKey.setKey(new Boolean(false));
                return;
            }
        }
    }

    private String id;
    private String name;
    private String token;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getToken() { return token; }

    public AuthToken(String id, String name, String token) {
        this.id = id;
        this.name = name;
        this.token = token;
    }
}
