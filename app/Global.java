import Dao.MongoDBDao;
import play.Application;
import play.GlobalSettings;

/**
 * Created by Administrator on 2015/12/2.
 */
public class Global extends GlobalSettings {

    public void onStop(Application app) {
        MongoDBDao.getInstance().close();
    }

    @Override
    public void onStart(Application application) {
        System.out.println(MongoDBDao.getInstance());
    }
}
