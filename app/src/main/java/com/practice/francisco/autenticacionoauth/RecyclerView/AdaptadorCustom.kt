package com.practice.francisco.autenticacionoauth.RecyclerView

import android.content.Context
import android.graphics.Color

import android.view.LayoutInflater
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.practice.francisco.autenticacionoauth.R
import com.practice.francisco.autenticacionoauth.Venue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.template_venues.view.*


class AdaptadorCustom(items:ArrayList<Venue>, var listener: ClickListener, var longClickListener: LongClickListener):RecyclerView.Adapter<AdaptadorCustom.ViewHolder>(){
    var items:ArrayList<Venue>? = null
    var multiSeleccion = false
    var itemsSeleccionados:ArrayList<Int>? = null
    var viewHolder:ViewHolder? = null
    var contexto:Context? = null

    init {
        this.items = items
        itemsSeleccionados = ArrayList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AdaptadorCustom.ViewHolder {
        contexto = parent?.context
        val vista = LayoutInflater.from(parent?.context).inflate(R.layout.template_venues,parent,false)
        viewHolder = ViewHolder(vista, listener, longClickListener)
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val item = items?.get(position)
        //holder.foto?.setImageResource(items?.foto!!)
        holder.nombre?.text = item?.name!!
        //val urlImagen = item?.categories?.get(0)!!.icon?.prefix
        if(item?.categories!!.size > 0){
            val urlImagen = item?.categories?.get(0)!!.icon?.prefix + "bg_64" + item?.categories?.get(0)!!.icon?.suffix
            Picasso.with(contexto).load(urlImagen).into(holder.foto)
        }


        //holder.precio?.text = "$"+items?.precio!!.toString()
        //holder.rating?.rating = items?.rating!!

        if (itemsSeleccionados?.contains(position)!!){
            holder.vista.setBackgroundColor(Color.LTGRAY)
        }else{
            holder.vista.setBackgroundColor(Color.WHITE)
        }
    }

    fun iniciarActionMode(){
        multiSeleccion = true
    }

    fun destruirActionMode(){
        multiSeleccion = false
        itemsSeleccionados?.clear()
        notifyDataSetChanged()
    }

    fun terminarActionMode(){
        //eliminar elementos seleccionados
        for(item in itemsSeleccionados!!){
            itemsSeleccionados?.remove(item)
        }
        multiSeleccion = false
        notifyDataSetChanged()
    }

    fun seleccionarItem(index:Int){
        if (multiSeleccion){
            if (itemsSeleccionados?.contains(index)!!){
                itemsSeleccionados?.remove(index)
            }else{
                itemsSeleccionados?.add(index)
            }
            notifyDataSetChanged()
        }
    }

    fun obtenerNumeroElementosSeleccionados():Int{
        return itemsSeleccionados?.count()!!
    }

    fun eliminarSeleccionados(){
        if (itemsSeleccionados?.count()!!>0){
            var itemsEliminados = ArrayList<Venue>()
            for (index in itemsSeleccionados!!){
                itemsEliminados.add(items?.get(index)!!)
            }
            items?.removeAll(itemsEliminados)
            itemsSeleccionados?.clear()
        }
    }

    class ViewHolder(vista:View, listener: ClickListener, longClickListener: LongClickListener):RecyclerView.ViewHolder(vista), View.OnClickListener, View.OnLongClickListener{
        var vista = vista
        var foto:ImageView? = null
        var nombre:TextView? = null
        //var precio:TextView? = null
        //var rating: RatingBar? = null
        var listener:ClickListener? = null
        var longListener:LongClickListener? = null
        init {
            //foto = vista.findViewById(R.id.ivFoto)
            //this.foto = vista.ivFoto as ImageView
            foto = vista.findViewById(R.id.ivFoto) as ImageView
            nombre = vista.findViewById(R.id.tvNombre) as TextView
            //precio = vista.findViewById(R.id.tvPrecio)
            //rating = vista.findViewById(R.id.rbRating)
            this.listener =  listener
            this.longListener = longClickListener
            vista.setOnClickListener(this)
            vista.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            this.listener?.onClick(v!!, adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            this.longListener?.longClick(v!!,adapterPosition)
            return true
        }
    }
}