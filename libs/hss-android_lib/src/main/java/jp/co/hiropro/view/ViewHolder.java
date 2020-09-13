package jp.co.hiropro.view;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class ViewHolder<T> extends RecyclerView.ViewHolder {
	protected View root;

	public ViewHolder(View root) {
		super(root);
		this.root = root;
		root.setTag(this);
	}
	/**
	 * Bind data of object into view
	 * @param item
	 */
	public abstract void bindData(T item);

	public <T>T findViewById(int resId) {
		return (T) root.findViewById(resId);
	}
}
