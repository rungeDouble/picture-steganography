#pragma version(1)
#pragma rs java_package_name(steganography)

uchar* out_string;
uint32_t width;
uint32_t out_len;

void root(uchar4* in, uint32_t x, uint32_t y){

    uint32_t ind_out= x + y*width;

    if(ind_out < out_len){
        uchar r= ((in->r) << 5) & 0xE0;
        uchar g= ((in->g) << 2) & 0x1C;
        uchar b= (in->b) & 0x03;

        uchar res_ch= 0x00;

        res_ch= res_ch | r;
        res_ch= res_ch | g;
        res_ch= res_ch | b;

        out_string[ind_out]= res_ch;
    }

}