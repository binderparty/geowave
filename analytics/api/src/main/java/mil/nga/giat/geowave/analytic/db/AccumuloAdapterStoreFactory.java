package mil.nga.giat.geowave.analytic.db;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.giat.geowave.analytic.ConfigurationWrapper;
import mil.nga.giat.geowave.analytic.param.CommonParameters;
import mil.nga.giat.geowave.analytic.param.DataStoreParameters;
import mil.nga.giat.geowave.analytic.param.GlobalParameters;
import mil.nga.giat.geowave.core.store.adapter.AdapterStore;
import mil.nga.giat.geowave.datastore.accumulo.BasicAccumuloOperations;
import mil.nga.giat.geowave.datastore.accumulo.metadata.AccumuloAdapterStore;

public class AccumuloAdapterStoreFactory implements
		AdapterStoreFactory
{
	final static Logger LOGGER = LoggerFactory.getLogger(AccumuloAdapterStoreFactory.class);

	@Override
	public AdapterStore getAdapterStore(
			ConfigurationWrapper context )
			throws InstantiationException {

		final String zookeeper = context.getString(
				DataStoreParameters.DataStoreParam.ZOOKEEKER,
				this.getClass(),
				"localhost:2181");
		final String accumuloInstance = context.getString(
				DataStoreParameters.DataStoreParam.ACCUMULO_INSTANCE,
				this.getClass(),
				"minInstance");

		BasicAccumuloOperations basicAccumuloOperations;
		try {
			basicAccumuloOperations = context.getInstance(
					CommonParameters.Common.ACCUMULO_CONNECT_FACTORY,
					this.getClass(),
					BasicAccumuloOperationsFactory.class,
					DirectBasicAccumuloOperationsFactory.class).build(
					zookeeper,
					accumuloInstance,
					context.getString(
							DataStoreParameters.DataStoreParam.ACCUMULO_USER,
							this.getClass(),
							"root"),
					context.getString(
							DataStoreParameters.DataStoreParam.ACCUMULO_PASSWORD,
							this.getClass(),
							""),
					context.getString(
							DataStoreParameters.DataStoreParam.ACCUMULO_NAMESPACE,
							this.getClass(),
							""));
		}
		catch (IllegalAccessException | AccumuloException | AccumuloSecurityException e) {
			LOGGER.error(
					"Cannot connect to GeoWave for Adapter Inquiry (" + accumuloInstance + "@ " + zookeeper + ")",
					e);
			throw new InstantiationException(
					"Cannot connect to GeoWave for Adapter Inquiry (" + accumuloInstance + "@ " + zookeeper + ")");
		}
		return new AccumuloAdapterStore(
				basicAccumuloOperations);

	}

}
