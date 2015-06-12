package mil.nga.giat.geowave.analytics.kmeans.mapreduce;

import mil.nga.giat.geowave.accumulo.BasicAccumuloOperations;
import mil.nga.giat.geowave.analytics.tools.dbops.BasicAccumuloOperationsFactory;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;

public class MockAccumuloOperationsFactory implements
		BasicAccumuloOperationsFactory
{
	final static MockInstance mockDataInstance = new MockInstance();
	static Connector mockDataConnector = null;

	public MockAccumuloOperationsFactory() {
		synchronized (mockDataInstance) {
			if (mockDataConnector == null) {
				try {
					mockDataConnector = mockDataInstance.getConnector(
							"root",
							new PasswordToken(
									new byte[0]));
				}
				catch (final AccumuloException e) {
					e.printStackTrace();
				}
				catch (final AccumuloSecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public BasicAccumuloOperations build(
			final String zookeeperUrl,
			final String instanceName,
			final String userName,
			final String password,
			final String tableNamespace )
			throws AccumuloException,
			AccumuloSecurityException {
		return new BasicAccumuloOperations(
				mockDataConnector);
	}

}
