/**
 * @author A00651678, Ryan Jennings
 * @version Sep 26, 2009
 */
package net.arg3.jmud.editors;

import net.arg3.jmud.EditorMetaData;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.view.AbstractCollectionView;

/**
 * @author Ryan Jennings
 * @version Sep 26, 2009
 * 
 */
public abstract class AbstractCollectionEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;
	Class<? extends IDataObject<?>> targetEntity;
	protected AbstractCollectionView editorPanel;

	public AbstractCollectionEditor() {
		editorPanel = createEditorPanel();

		add(editorPanel);
	}

	public Class<?> getTargetEntity() {
		return targetEntity;
	}

	public void setEditor(Object value) {
		if (targetEntity == null)
			return;
		editorPanel.setEditor(EditorMetaData.get(targetEntity), value);
		editorPanel.setTitle(targetEntity.getSimpleName() + " Editor");
	}

	@Override
	public void setReadOnly(boolean value) {
		editorPanel.setReadOnly(value);
	}

	@SuppressWarnings("unchecked")
	public void setTargetEntity(Class<?> class1) {
		targetEntity = (Class<? extends IDataObject<?>>) class1;
	}

	protected abstract AbstractCollectionView createEditorPanel();
}
