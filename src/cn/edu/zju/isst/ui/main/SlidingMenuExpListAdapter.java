/**
 * 
 */
package cn.edu.zju.isst.ui.main;

import java.util.List;
import java.util.Map;

import cn.edu.zju.isst.R;
import cn.edu.zju.isst.util.L;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 侧拉菜单ExpandableListAdapter类
 * 
 * @author theasir
 * 
 */
public class SlidingMenuExpListAdapter extends BaseExpandableListAdapter {

	private Activity m_actiContext;
	private List<String> m_listGroupNames;
	private Map<String, List<String>> m_mapGroupCollection;
	private SELECTED m_selectedIndex;

	/**
	 * 
	 */
	public SlidingMenuExpListAdapter(Activity context, List<String> groupNames,
			Map<String, List<String>> groupCollection) {
		this.m_actiContext = context;
		this.m_listGroupNames = groupNames;
		this.m_mapGroupCollection = groupCollection;
		this.m_selectedIndex = new SELECTED();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return m_listGroupNames.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		if (!m_mapGroupCollection.containsKey(m_listGroupNames
				.get(groupPosition)))
			return 0;
		return m_mapGroupCollection.get(m_listGroupNames.get(groupPosition))
				.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return m_listGroupNames.get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return m_mapGroupCollection.get(m_listGroupNames.get(groupPosition))
				.get(childPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String nav = (String) getGroup(groupPosition);

		View tempView = m_actiContext.getLayoutInflater().inflate(
				R.layout.sm_group_item, null);
		// if (convertView == null) {
		// convertView = m_actiContext.getLayoutInflater().inflate(
		// R.layout.sm_group_item, null);
		// }
		TextView mainNav = (TextView) tempView
				.findViewById(R.id.sm_group_item_nav);
		ImageView mainImage = (ImageView) tempView
				.findViewById(R.id.sm_group_item_img);
		mainNav.setTypeface(null, Typeface.BOLD);
		mainNav.setText(nav);
		if (getChildrenCount(groupPosition) > 0) {
			if (isExpanded) {
				mainImage.setImageResource(R.drawable.ic_expand);
			} else {
				mainImage.setImageResource(R.drawable.ic_not_expand);
			}
		} else {
			mainImage.setVisibility(View.GONE);
			if (m_selectedIndex.groupIndex == groupPosition) {
				tempView.setBackgroundColor(Color.BLUE);
			} 
			L.i("CarpeDiem", "groupIndex =" + m_selectedIndex.groupIndex);
		}
		return tempView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String nav = (String) getChild(groupPosition, childPosition);
		if (convertView == null) {
			convertView = m_actiContext.getLayoutInflater().inflate(
					R.layout.sm_child_item, null);
		}
		TextView subNav = (TextView) convertView
				.findViewById(R.id.sm_child_item_nav);
		subNav.setText(nav);
		if (groupPosition == m_selectedIndex.groupIndex
				&& childPosition == m_selectedIndex.childIndex) {
			convertView.setBackgroundColor(Color.BLUE);
		} 
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setGoupIndex(int groupIndex) {
		this.m_selectedIndex.setGroupIndex(groupIndex);
	}

	public void setChildIndex(int childIndex) {
		this.m_selectedIndex.setChildIndex(childIndex);
	}

	public class SELECTED {
		private int groupIndex;
		private int childIndex;

		public SELECTED() {
			setGroupIndex(0);
			setChildIndex(-1);
		}

		public int getGroupIndex() {
			return groupIndex;
		}

		public void setGroupIndex(int groupIndex) {
			this.groupIndex = groupIndex;
		}

		public int getChildIndex() {
			return childIndex;
		}

		public void setChildIndex(int childIndex) {
			this.childIndex = childIndex;
		}
	};

}
