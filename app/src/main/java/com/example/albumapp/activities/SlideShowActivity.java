package com.example.albumapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.albumapp.R;
import com.example.albumapp.adapters.SlideShowAdapter;
import com.example.albumapp.models.MyImage;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class SlideShowActivity extends AppCompatActivity {
    private SliderView sliderView;
    private ImageView img_back_slide_show;
    private Toolbar toolbar_slide;
    private ArrayList<MyImage> imageList;
    private Intent intent;
    private List<SliderAnimations> effect = new ArrayList<SliderAnimations>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        intent = getIntent();
        mappingControls();
        event();

    }

    private void event() {
        addListAnim();
        setUpSlider(0);
        setUpToolBar();
        img_back_slide_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    private void setUpToolBar() {
        toolbar_slide.inflateMenu(R.menu.menu_effect_slide);
        toolbar_slide.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_effect1)
                    setUpSlider(0);
                else if (id == R.id.menu_effect2)
                    setUpSlider(1);
                else if (id == R.id.menu_effect3)
                    setUpSlider(2);
                else if (id == R.id.menu_effect4)
                    setUpSlider(3);
                else if (id == R.id.menu_effect5)
                    setUpSlider(4);
                return true;
            }
        });
    }

    private void addListAnim() {
        effect.add(SliderAnimations.SIMPLETRANSFORMATION);
        effect.add(SliderAnimations.FADETRANSFORMATION);
        effect.add(SliderAnimations.ZOOMOUTTRANSFORMATION);
        effect.add(SliderAnimations.DEPTHTRANSFORMATION);
        effect.add(SliderAnimations.SPINNERTRANSFORMATION);
    }

    private void setUpSlider(int sliderAnimation) {
        imageList = intent.getParcelableArrayListExtra("dataImages");
        SlideShowAdapter slideShowAdapter = new SlideShowAdapter();
        slideShowAdapter.setData(imageList);
        sliderView.setSliderAdapter(slideShowAdapter);
        sliderView.startAutoCycle();
        sliderView.setSliderTransformAnimation(effect.get(sliderAnimation));

    }

    private void mappingControls() {
        sliderView = findViewById(R.id.sliderView);
        img_back_slide_show = findViewById(R.id.img_back_slide_show);
        toolbar_slide = findViewById(R.id.toolbar_slide);
    }
}