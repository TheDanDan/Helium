package com.example.helium.ui.slideshow;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.helium.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private ActivityResultLauncher<Intent> folderSelectorLauncher;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Button
        View folderSelectorButton = root.findViewById(binding.FolderSelector.getId());

        // ActivityResultLauncher
        folderSelectorLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri treeUri = data.getData();
                            Log.d("URI Name", treeUri.toString());

                            // Persist the permission
                            requireActivity().getContentResolver().takePersistableUriPermission(treeUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                    }
                });

        folderSelectorButton.setOnClickListener(view -> showFilePicker());

        return root;
    }

    private void printFileNames(Uri treeUri) {
        ContentResolver contentResolver = requireActivity().getContentResolver();

        // Retrieve the document tree root using the treeUri
        DocumentFile root = DocumentFile.fromTreeUri(requireContext(), treeUri);

        if (root != null && root.exists()) {
            // Traverse through the files and print their names
            for (DocumentFile file : root.listFiles()) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    Log.d("File Name", fileName);
                }
            }
        }
    }

    private void showFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        // Start the ActivityResultLauncher to launch the folder selector
        folderSelectorLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}