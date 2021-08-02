package it.unipd.stage.sl;

import io.smallrye.mutiny.Uni;
import it.unipd.stage.sl.lib.rsa.RsaManager;
import it.unipd.stage.sl.reactive.RsaReactive;
import org.junit.jupiter.api.Test;

public class UniRsaWrapperTests {

    @Test
    public void testUniException() {
        RsaManager m = RsaManager.fromGenerator(16, 10, false);
        Uni<RsaManager> rm = RsaReactive.getReactiveRsaManager(m);
        Uni<String> s = RsaReactive.encryptReactiveRsa(rm, "abcygghjd");
        s.subscribe().with(
                System.out::println,
                fail -> System.out.println(fail.getCause().getMessage())
        );
        //String c = RsaReactive.checkedAwait(s);
    }
}
