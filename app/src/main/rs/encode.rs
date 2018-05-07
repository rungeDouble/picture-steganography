#pragma version(1)
#pragma rs java_package_name(steganography)

uchar* input_string;
uint32_t string_length; //lenght of input_string
uint32_t width; //width of the image -15

void root(uchar4* in, uint32_t x, uint32_t y) {

  uint32_t ind_str= x + y*width;

  if(ind_str<string_length){

    uchar r= (in->r) & 0xF8;
    uchar g= (in->g) & 0xF8;
    uchar b= (in->b) & 0xFC;

    uchar r_ch= ((input_string[ind_str]) >> 5) & 0x07;
    uchar g_ch= ((input_string[ind_str]) >> 2) & 0x07;
    uchar b_ch= (input_string[ind_str]) & 0x03;

    r= r | r_ch;
    g= g | g_ch;
    b= b | b_ch;

    in->r= r;
    in->g= g;
    in->b= b;
  }

}