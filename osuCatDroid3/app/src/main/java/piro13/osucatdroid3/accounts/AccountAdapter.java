package piro13.osucatdroid3.accounts;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import piro13.osucatdroid3.R;

public class AccountAdapter extends ListAdapter<Account, AccountAdapter.AccountHolder> {
    private OnItemClickListener listener;

    public AccountAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Account> DIFF_CALLBACK = new DiffUtil.ItemCallback<Account>() {
        @Override
        public boolean areItemsTheSame(@NonNull Account account, @NonNull Account t1) {
            return account.getId() == t1.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Account account, @NonNull Account t1) {
            return account.getName().equals(t1.getName()) &&
                    account.getPassword().equals(t1.getPassword()) &&
                    account.getApi().equals(t1.getApi());
        }
    };

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_item, viewGroup, false);
        return new AccountHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder accountHolder, int i) {
        Account currentAccount = getItem(i);
        accountHolder.textViewName.setText(currentAccount.getName());
        String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/avatars/" + currentAccount.getIdPpy() + ".jpeg";
        File file = new File(path);
        if(file.exists()){
            Picasso.get().load(file).into(accountHolder.avatar);
        }else{
            //it works
            Picasso.get().load(R.drawable.ic_profile).placeholder(R.drawable.ic_profile).into(accountHolder.avatar);
        }

    }

    public Account getAccountAt(int position) {
        return getItem(position);
    }

    class AccountHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private ImageView avatar;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            avatar = itemView.findViewById(R.id.account_item_avatar);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Account account);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
